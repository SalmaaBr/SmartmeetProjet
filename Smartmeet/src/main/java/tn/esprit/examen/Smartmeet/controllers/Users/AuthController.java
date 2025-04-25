package tn.esprit.examen.Smartmeet.controllers.Users;


import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.Smartmeet.ResReq.request.LoginRequest;
import tn.esprit.examen.Smartmeet.ResReq.request.SignupRequest;
import tn.esprit.examen.Smartmeet.ResReq.response.JwtResponse;
import tn.esprit.examen.Smartmeet.ResReq.response.MessageResponse;
import tn.esprit.examen.Smartmeet.Services.AuthenticationService;
import tn.esprit.examen.Smartmeet.Services.AuthenticationServiceImpl;
import tn.esprit.examen.Smartmeet.Services.UserService;
import tn.esprit.examen.Smartmeet.entities.Users.BlacklistedToken;
import tn.esprit.examen.Smartmeet.entities.Users.TypeUserRole;
import tn.esprit.examen.Smartmeet.entities.Users.Users;
import tn.esprit.examen.Smartmeet.repositories.Users.BlacklistedTokenRepository;
import tn.esprit.examen.Smartmeet.repositories.Users.UserRepository;
import tn.esprit.examen.Smartmeet.security.jwt.JwtUtils;
import tn.esprit.examen.Smartmeet.security.services.UserDetailsImpl;


import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
@RestController
@RequestMapping("/api/auth")

@RequiredArgsConstructor
public class AuthController {
	private final BlacklistedTokenRepository blacklistedTokenRepository;
	private final AuthenticationService service;
	private final AuthenticationManager authenticationManager;
	private final JwtUtils jwtUtils;
	private final UserRepository userRepository;
	private final PasswordEncoder encoder;
	private final UserService userService;


	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		System.out.println("Attempting to authenticate user: " + loginRequest.getUsername());

		try {
			System.out.println("Before authentication attempt...");

			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
			);

			System.out.println("Authentication successful!");

			SecurityContextHolder.getContext().setAuthentication(authentication);
			String jwt = jwtUtils.generateJwtToken(authentication);

			UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
			List<String> roles = userDetails.getAuthorities().stream()
					.map(item -> item.getAuthority())
					.collect(Collectors.toList());
			Users user =  userService.getUserByEmail(userDetails.getEmail());
			if (user.isEnabled()){
				System.out.println("User authenticated successfully: " + userDetails.getUsername());
				return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));

			}else{
				System.out.println("User authentication failed: " + userDetails.getUsername() + " is not active");

				return ResponseEntity.status(HttpStatus.FORBIDDEN)
						.body(Map.of(
								"status", HttpStatus.FORBIDDEN.value(),
								"error", "User Not Active",
								"message", "Your account is not activated. Please contact support.",
								"timestamp", Instant.now()
						));
			}

		} catch (BadCredentialsException e) {
			System.out.println("Invalid username or password");
			e.printStackTrace();  // Affiche l'erreur complète dans la console
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");

		} catch (Exception e) {
			System.out.println("Authentication failed due to an unexpected error.");
			e.printStackTrace();  // Affiche l'erreur exacte
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: " + e.getMessage());
		}
	}


	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		System.out.println("--------------------------");
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
		}

		// Création d'un nouvel utilisateur
		Users user = new Users(signUpRequest.getUsername(),
				signUpRequest.getEmail(),
				encoder.encode(signUpRequest.getPassword()));

		// Récupération des rôles depuis la requête
		Set<TypeUserRole> roles = new HashSet<>();

		if (signUpRequest.getRoles() != null && !signUpRequest.getRoles().isEmpty()) {
			for (TypeUserRole role : signUpRequest.getRoles()) {
				// Comparaison directe des rôles
				if (role == TypeUserRole.ADMIN) {
					roles.add(TypeUserRole.ADMIN);
				} else if (role == TypeUserRole.USER) {
					roles.add(TypeUserRole.USER);
				} else {
					return ResponseEntity.badRequest().body(new MessageResponse("Error: Role " + role + " not found!"));
				}
			}
		} else {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Role not found!"));
		}

		// Si aucun rôle n'est trouvé, attribuer un rôle par défaut
		if (roles.isEmpty()) {
			roles.add(TypeUserRole.USER); // rôle par défaut
		}

		user.setRoles(roles);
		user.setEnabled(false);
		// Enregistrer l'utilisateur dans la base de données
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));

	}




	@PostMapping("/logout")
	public ResponseEntity<?> logout(@RequestHeader("Authorization") String tokenHeader) {
		if (tokenHeader == null || !tokenHeader.startsWith("Bearer ")) {
			return ResponseEntity.badRequest().body("Token invalide");
		}

		String token = tokenHeader.substring(7);
		BlacklistedToken blacklistedToken = new BlacklistedToken();
		blacklistedToken.setToken(token);
		blacklistedToken.setExpirationDate(new Date(System.currentTimeMillis() + 3600000)); // Expiration après 1h

		blacklistedTokenRepository.save(blacklistedToken);

		SecurityContextHolder.clearContext(); // Supprimer les informations d'authentification en mémoire
		return ResponseEntity.ok("Déconnexion réussie");

	}

	@GetMapping("/activate-account")
	public void confirm(
			@RequestParam String token
	) throws MessagingException {
		service.activateAccount(token);
	}
	@PostMapping("/reset-password-request")
	public ResponseEntity<?> resetPasswordRequest(@RequestParam String email) {
		try {
			service.initiatePasswordReset(email);
			return ResponseEntity.ok("Password reset email sent");
		} catch (UsernameNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
		} catch (MessagingException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email");
		}
	}

	@GetMapping("/validate-reset-token")
	public ResponseEntity<?> validateResetToken(@RequestParam String token) {
		try {
			service.validatePasswordResetToken(token);
			return ResponseEntity.ok("Token is valid");
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

		@PostMapping("/reset-password")
		public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
			try {
				service.resetPassword(request.getToken(), request.getNewPassword());
				return ResponseEntity.ok("Password reset successfully");
			} catch (RuntimeException e) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
			}
		}

	//recuperer user connecter
	@GetMapping("/me")
	public ResponseEntity<Users> getCurrentUser(Authentication authentication) {
		String username = authentication.getName();
		Users user = userService.findByUsername(username)
				.orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
		return ResponseEntity.ok(user);
	}
	}

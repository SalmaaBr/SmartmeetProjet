package tn.esprit.examen.Smartmeet.controllers.Users;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.Smartmeet.ResReq.request.LoginRequest;
import tn.esprit.examen.Smartmeet.ResReq.request.SignupRequest;
import tn.esprit.examen.Smartmeet.ResReq.response.JwtResponse;
import tn.esprit.examen.Smartmeet.ResReq.response.MessageResponse;
import tn.esprit.examen.Smartmeet.entities.Users.BlacklistedToken;
import tn.esprit.examen.Smartmeet.entities.Users.TypeUserRole;
import tn.esprit.examen.Smartmeet.entities.Users.Users;
import tn.esprit.examen.Smartmeet.repositories.Users.BlacklistedTokenRepository;
import tn.esprit.examen.Smartmeet.repositories.Users.UserRepository;
import tn.esprit.examen.Smartmeet.security.jwt.JwtUtils;
import tn.esprit.examen.Smartmeet.security.services.UserDetailsImpl;


import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
@RestController
@RequestMapping("/api/auth")

public class AuthController {
	@Autowired
	 AuthenticationManager authenticationManager;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	UserRepository userRepository;

	@Autowired
	 PasswordEncoder encoder;



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

			System.out.println("User authenticated successfully: " + userDetails.getUsername());
			return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));

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

		// Enregistrer l'utilisateur dans la base de données
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));

	}

	private final BlacklistedTokenRepository blacklistedTokenRepository;

	public AuthController(BlacklistedTokenRepository blacklistedTokenRepository) {
		this.blacklistedTokenRepository = blacklistedTokenRepository;
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



}

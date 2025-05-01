package tn.esprit.examen.Smartmeet.controllers.Users;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.Smartmeet.Services.IUsersServices;
import tn.esprit.examen.Smartmeet.Services.UserService;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.TypeTheme;
import tn.esprit.examen.Smartmeet.entities.Users.Users;
import tn.esprit.examen.Smartmeet.repositories.Users.UserRepository;
import tn.esprit.examen.Smartmeet.security.jwt.JwtUtils;

import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;

    }

    @PostMapping
    public ResponseEntity<Users> createUser(@RequestBody Users user) {
        Users savedUser = userService.createUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Users> getUserById(@PathVariable Long id) {
        Users user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<Users>> getAllUsers() {
        List<Users> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Users> updateUser(@PathVariable Long id, @RequestBody Users userDetails) {
        Users updatedUser = userService.updateUser(id, userDetails);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/by-email")
    public ResponseEntity<Users> getUserByEmail(@RequestParam String email) {
        Users user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    //recuperer user connecter
    @GetMapping("/me")
    public ResponseEntity<Users> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        Users user = userService.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}/interests")
    public ResponseEntity<Set<TypeTheme>> getUserInterests(@PathVariable Long id) {
        Users user = userService.getUserById(id);
        return ResponseEntity.ok(user.getInterests());
    }

    @GetMapping("/me/interests")
    public ResponseEntity<Set<TypeTheme>> getCurrentUserInterests(Authentication authentication) {
        String username = authentication.getName();
        Users user = userService.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return ResponseEntity.ok(user.getInterests());
    }

    @GetMapping("/me/recommendations")
    public ResponseEntity<List<Users>> getUserRecommendations(Authentication authentication) {
        String username = authentication.getName();
        Users currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<Users> recommendedUsers = userService.recommendUsersWithCommonInterests(currentUser);
        return ResponseEntity.ok(recommendedUsers);
    }

}
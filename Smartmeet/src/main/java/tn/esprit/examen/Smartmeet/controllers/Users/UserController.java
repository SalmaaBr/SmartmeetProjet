package tn.esprit.examen.Smartmeet.controllers.Users;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.examen.Smartmeet.Services.UserService;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.TypeTheme;
import tn.esprit.examen.Smartmeet.entities.Users.Users;
import tn.esprit.examen.Smartmeet.repositories.Users.UserRepository;
import tn.esprit.examen.Smartmeet.security.jwt.JwtUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
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

    @PostMapping(value = "/upload-profile-image/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> uploadProfileImage(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = userService.uploadProfileImage(userId, file);
            Map<String, String> response = new HashMap<>();
            response.put("imageUrl", imageUrl);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Could not upload profile image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping(value = "/profile-image/{userId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getProfileImage(@PathVariable Long userId) {
        try {
            byte[] imageBytes = userService.getProfileImage(userId);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageBytes);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
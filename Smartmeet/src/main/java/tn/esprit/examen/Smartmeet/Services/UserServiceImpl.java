package tn.esprit.examen.Smartmeet.Services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.examen.Smartmeet.entities.Users.Users;
import tn.esprit.examen.Smartmeet.repositories.Users.BlacklistedTokenRepository;
import tn.esprit.examen.Smartmeet.repositories.Users.UserRepository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    public UserServiceImpl(UserRepository userRepository, BlacklistedTokenRepository blacklistedTokenRepository) {
        this.userRepository = userRepository;
        this.blacklistedTokenRepository = blacklistedTokenRepository;
    }

    @Override
    public Users createUser(Users user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Users getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Users updateUser(Long id, Users userDetails) {
        Users existingUser = getUserById(id);

        // Update only non-null fields
        if (userDetails.getUsername() != null) {
            existingUser.setUsername(userDetails.getUsername());
        }
        if (userDetails.getEmail() != null) {
            existingUser.setEmail(userDetails.getEmail());
        }
        // Ne mettez à jour le mot de passe que s'il est fourni
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            existingUser.setPassword(userDetails.getPassword());
        }
        if (userDetails.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(userDetails.getPhoneNumber());
        }
        if (userDetails.getAddress() != null) {
            existingUser.setAddress(userDetails.getAddress());
        }
        existingUser.setEnabled(userDetails.isEnabled());

        // Gestion des rôles et intérêts
        if (userDetails.getUserRole() != null) {
            existingUser.setRoles(userDetails.getUserRole());
        }
        if (userDetails.getInterests() != null) {
            existingUser.setInterests(userDetails.getInterests());
        }

        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(Long id) {
        Users user = getUserById(id);
        blacklistedTokenRepository.deleteByUserUserID(user.getUserID());

        userRepository.delete(user);
    }

    @Override
    public Users getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
    }

    // UserService.java
    @Override
    public Optional<Users> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public String uploadProfileImage(Long userId, MultipartFile file) throws IOException {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // Store the image data in the database
        user.setProfileImage(file.getBytes());
        
        // Store the original filename or generate a unique name
        String fileName = userId + "_" + file.getOriginalFilename();
        user.setImagePath(fileName);
        
        userRepository.save(user);
        return fileName;
    }

    @Override
    public byte[] getProfileImage(Long userId) throws IOException {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        if (user.getProfileImage() == null) {
            throw new IOException("Profile image not found");
        }

        return user.getProfileImage();
    }
}
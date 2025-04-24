package tn.esprit.examen.Smartmeet.Services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.examen.Smartmeet.entities.Users.Users;
import tn.esprit.examen.Smartmeet.repositories.Users.BlacklistedTokenRepository;
import tn.esprit.examen.Smartmeet.repositories.Users.UserRepository;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, BlacklistedTokenRepository blacklistedTokenRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.blacklistedTokenRepository = blacklistedTokenRepository;
        this.passwordEncoder = passwordEncoder;
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

        // Update only non-empty fields
        if (userDetails.getUsername() != null && !userDetails.getUsername().isEmpty()) {
            existingUser.setUsername(userDetails.getUsername());
        }
        if (userDetails.getEmail() != null && !userDetails.getEmail().isEmpty()) {
            existingUser.setEmail(userDetails.getEmail());
        }
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            // Encode the new password
            existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        if (userDetails.getPhoneNumber() != null && !userDetails.getPhoneNumber().isEmpty()) {
            existingUser.setPhoneNumber(userDetails.getPhoneNumber());
        }
        if (userDetails.getAddress() != null && !userDetails.getAddress().isEmpty()) {
            existingUser.setAddress(userDetails.getAddress());
        }
        if (userDetails.getUserRole() != null) {
            existingUser.setRoles(userDetails.getUserRole());
        }
        // Only update enabled if it's explicitly included in the request
        // Since isEnabled() returns a boolean primitive, we'll update it if it's different from the current value
        if (userDetails.isEnabled() != existingUser.isEnabled()) {
            existingUser.setEnabled(userDetails.isEnabled());
        }

        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(Long id) {
        Users user = getUserById(id);
        
        // Delete all related entities
        user.getComments().clear();
        user.getPublicationLike().clear();
        user.getInteractivePublications().clear();
        user.getMentalHealths().clear();
        user.getEvents().clear();
        user.getMonitoringrecruitments().clear();
        user.getDocuments().clear();
        user.getReportedItems().clear();
        user.getClaims().clear();
        user.getResourceReservations().clear();
        if (user.getSponsorsGeres() != null) {
            user.getSponsorsGeres().clear();
        }
        
        // Delete blacklisted tokens
        blacklistedTokenRepository.deleteByUserUserID(user.getUserID());
        
        // Now we can safely delete the user
        userRepository.delete(user);
    }

    @Override
    public Users getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
    }
}
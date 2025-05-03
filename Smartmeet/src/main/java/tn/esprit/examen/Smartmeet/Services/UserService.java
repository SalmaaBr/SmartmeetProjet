package tn.esprit.examen.Smartmeet.Services;

import tn.esprit.examen.Smartmeet.entities.Users.Users;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {
    Users createUser(Users user);
    Users getUserById(Long id);
    List<Users> getAllUsers();
    Users updateUser(Long id, Users userDetails);
    void deleteUser(Long id);
    Users getUserByEmail(String email);
    Optional<Users> findByUsername(String username);
    String uploadProfileImage(Long userId, MultipartFile file) throws IOException;
    byte[] getProfileImage(Long userId) throws IOException;
}
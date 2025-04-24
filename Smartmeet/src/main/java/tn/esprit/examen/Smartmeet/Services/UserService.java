package tn.esprit.examen.Smartmeet.Services;

import tn.esprit.examen.Smartmeet.entities.Users.Users;

import java.util.List;
import java.util.Set;

public interface UserService {
    Users createUser(Users user);
    Users getUserById(Long id);
    List<Users> getAllUsers();
    Users updateUser(Long id, Users userDetails);
    void deleteUser(Long id);
    Users getUserByEmail(String email);

}
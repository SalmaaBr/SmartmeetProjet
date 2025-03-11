package tn.esprit.examen.Smartmeet.Services;

import tn.esprit.examen.Smartmeet.entities.Users.Users;

import java.util.List;

public interface IUsersServices {

    Users createUser(Users user);
    List<Users> findAll();
    Users findById(Long id);
    Users update(Long id, Users user);
    void delete(Long id);
}

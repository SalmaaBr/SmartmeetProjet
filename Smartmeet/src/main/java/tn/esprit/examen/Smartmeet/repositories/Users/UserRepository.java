package tn.esprit.examen.Smartmeet.repositories.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.examen.Smartmeet.entities.Users.Users;


import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);
    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);


    Optional<Users> findByEmail(String email);
}

package tn.esprit.examen.Smartmeet.repositories.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.examen.Smartmeet.entities.Users.BlacklistedToken;

import java.util.Optional;

@Repository
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, String> {
    Optional<BlacklistedToken> findByToken(String token);

    void deleteByUserUserID(Long userId);
}

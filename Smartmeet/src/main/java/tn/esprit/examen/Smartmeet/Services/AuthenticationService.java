package tn.esprit.examen.Smartmeet.Services;

import jakarta.mail.MessagingException;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.examen.Smartmeet.entities.Users.Users;

public interface AuthenticationService {
    void initiatePasswordReset(String email) throws MessagingException;

    String generateAndSavePasswordResetToken(Users user);

    void sendPasswordResetEmail(Users user, String token) throws MessagingException;

    void validatePasswordResetToken(String token);

    void resetPassword(String token, String newPassword);

    @Transactional
    void activateAccount(String token) throws MessagingException;

    String generateAndSaveActivationToken(Users user);

    void sendValidationEmail(Users user) throws MessagingException;

    String generateActivationCode(int length);
}

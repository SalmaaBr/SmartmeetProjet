package tn.esprit.examen.Smartmeet.Services;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.examen.Smartmeet.email.EmailService;
import tn.esprit.examen.Smartmeet.email.EmailTemplateName;
import tn.esprit.examen.Smartmeet.entities.Users.BlacklistedToken;
import tn.esprit.examen.Smartmeet.entities.Users.Users;
import tn.esprit.examen.Smartmeet.repositories.Users.BlacklistedTokenRepository;
import tn.esprit.examen.Smartmeet.repositories.Users.UserRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service

public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final BlacklistedTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    @Value("${application.mailing.frontend.reset-password-url}")
    private String resetPasswordUrl;

    @Override
    public void initiatePasswordReset(String email) throws MessagingException {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        String token = generateAndSavePasswordResetToken(user);
        sendPasswordResetEmail(user, token);
    }
    @Override
    public String generateAndSavePasswordResetToken(Users user) {
        String token = generateActivationCode(6);
        var resetToken = BlacklistedToken.builder()
                .token(token)
                .user(user)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();
        tokenRepository.save(resetToken);
        return token;
    }
    @Override
    public void sendPasswordResetEmail(Users user, String token) throws MessagingException {
        String resetUrl = resetPasswordUrl +"?token=" + token;
        emailService.sendEmail(
                user.getEmail(),
                user.getUsername(),
                EmailTemplateName.RESET_PASSWORD,
                resetUrl,
                token,
                "Password Reset Request"
        );
    }
    @Override
    public void validatePasswordResetToken(String token) {
        BlacklistedToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        if (LocalDateTime.now().isAfter(resetToken.getExpiresAt())) {
            throw new RuntimeException("Token has expired");
        }
    }
    @Override
    public void resetPassword(String token, String newPassword) {
        BlacklistedToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        if (LocalDateTime.now().isAfter(resetToken.getExpiresAt())) {
            throw new RuntimeException("Token has expired");
        }
        Users userFromToken = resetToken.getUser();
        Users user = userService.getUserByEmail(userFromToken.getEmail());

        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);
        resetToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(resetToken);
    }

    @Override
    @Transactional
    public void activateAccount(String token) throws MessagingException {
        BlacklistedToken savedToken = tokenRepository.findByToken(token)
                // todo exception has to be defined
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("Activation token has expired. A new token has been send to the same email address");
        }

        var user = userRepository.findById(savedToken.getUser().getUserID())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);

        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }
    @Override
    public String generateAndSaveActivationToken(Users user) {
        // Generate a token
        String generatedToken = generateActivationCode(6);
        var token = BlacklistedToken.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);

        return generatedToken;
    }
    @Override
    public void sendValidationEmail(Users user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);


            emailService.sendEmail(
                    user.getEmail(),
                    user.getUsername(),
                    EmailTemplateName.ACTIVATE_ACCOUNT,
                    activationUrl,
                    newToken,
                    "Account activation"
            );

    }
    @Override
    public String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();

        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }
}

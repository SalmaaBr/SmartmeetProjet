package tn.esprit.examen.Smartmeet.Services.MaryemSalhi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MailingService {

    private static final Logger logger = LoggerFactory.getLogger(MailingService.class);

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationCode(String toEmail, String verificationCode) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("New Feedback Submitted"); // Match the subject from the controller
            message.setText(verificationCode); // Use the passed message content
            mailSender.send(message);
            logger.info("Email sent successfully to {}", toEmail);
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email à " + toEmail, e);
            throw new RuntimeException("Erreur lors de l'envoi de l'email");
        }
    }
}
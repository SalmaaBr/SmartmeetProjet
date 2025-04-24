package tn.esprit.examen.Smartmeet.email;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    @Async
    public void sendEmail(String to, String username, EmailTemplateName template,
                          String resetUrl, String token, String subject) {
        try {
            if (to == null) {
                LOGGER.error("Error sending email: recipient address is null");
                return;
            }

            Context context = new Context();
            context.setVariable("username", username);
            context.setVariable("resetUrl", resetUrl);
            context.setVariable("token", token);

            // Process Thymeleaf template
            String htmlContent = templateEngine.process( template.getName(), context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setTo(to);
            helper.setFrom("sps2022noreply@gmail.com");
            helper.setSubject(subject);
            helper.setText(htmlContent,true); // true = HTML content

            mailSender.send(mimeMessage);
            LOGGER.info("HTML email sent successfully to {}", to);



        } catch (Exception e) {
            LOGGER.error("Error sending email to {}: {}", to, e.getMessage());
        }
    }

    /**
     * Send a simple email message
     *
     * @param to recipient email address
     * @param subject email subject
     * @param text email body text
     */
    public void sendSimpleMessage(String to, String subject, String text) {
        try {
            log.info("Sending email to: {}, subject: {}", to, subject);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);

            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
            // Don't rethrow, just log the error to avoid breaking the caller's flow
        }
    }
}
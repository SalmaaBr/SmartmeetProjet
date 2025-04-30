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
    @Async
    public void sendEventUpdateEmail(String to, String username, String eventTitle) {
        try {
            Context context = new Context();
            context.setVariable("username", username);
            context.setVariable("subject", eventTitle);

            String htmlContent = templateEngine.process("event_updated", context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setTo(to);
            helper.setFrom("sps2022noreply@gmail.com");
            helper.setSubject("🔔 Event Update : " + eventTitle);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            LOGGER.info("✅ Email envoyé pour mise à jour d'événement à {}", to);
        } catch (Exception e) {
            LOGGER.error("❌ Erreur en envoyant le mail de mise à jour : {}", to, e);
        }
    }

    @Async
    public void sendMeetingInvitationEmail(String to, String username, String meetingName, String organizer,
                                           String participant, String startTime, int duration, String meetingLink) {
        try {
            if (to == null) {
                LOGGER.error("Error sending meeting invitation email: recipient address is null");
                return;
            }

            Context context = new Context();
            context.setVariable("username", username);
            context.setVariable("meetingName", meetingName);
            context.setVariable("organizer", organizer);
            context.setVariable("participant", participant);
            context.setVariable("startTime", startTime);
            context.setVariable("duration", duration);
            context.setVariable("meetingLink", meetingLink);

            // Process Thymeleaf template
            String htmlContent = templateEngine.process("meeting_invitation", context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setTo(to);
            helper.setFrom("sps2022noreply@gmail.com");
            helper.setSubject("Invitation à la réunion : " + meetingName);
            helper.setText(htmlContent, true); // true = HTML content

            mailSender.send(mimeMessage);
            LOGGER.info("Meeting invitation email sent successfully to {}", to);

        } catch (Exception e) {
            LOGGER.error("Error sending meeting invitation email to {}: {}", to, e.getMessage());
        }
    }


}
package tn.esprit.examen.Smartmeet.Services.GhanemRidene;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.LostAndFound;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.LostAndFoundType;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.Notification;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.dto.NotificationDto;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Event;
import tn.esprit.examen.Smartmeet.entities.Users.Users;
import tn.esprit.examen.Smartmeet.repositories.GhanemRidene.NotificationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender emailSender;

    @Override
    public void notifyEventParticipants(LostAndFound lostAndFound) {
        Event event = lostAndFound.getEvent();
        Set<Users> participants = event.getUsers();
        Users creator = lostAndFound.getCreator();
        
        String itemType = lostAndFound.getType() == LostAndFoundType.LOST ? "lost" : "found";
        String title = "New " + itemType + " item reported";
        String message = creator.getUsername() + " has reported a " + itemType + 
                         " item: " + lostAndFound.getTitle() + " at event: " + event.getTitle();
        
        for (Users participant : participants) {
            // Skip notification for the creator
            if (!participant.getUserID().equals(creator.getUserID())) {
                // Store in-app notification
                notifyUser(participant, title, message, lostAndFound.getId(), "LOST_AND_FOUND");
                
                // Send email notification if email is available
                if (participant.getEmail() != null && !participant.getEmail().isEmpty()) {
                    sendEmailNotification(participant.getEmail(), title, message);
                }
            }
        }
    }

    @Override
    public void notifyUser(Users user, String title, String message, Long relatedItemId, String type) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setTimestamp(LocalDateTime.now());
        notification.setRead(false);
        notification.setRelatedItemId(relatedItemId);
        notification.setType(type);
        
        notificationRepository.save(notification);
    }

    @Override
    public void sendEmailNotification(String email, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        
        try {
            emailSender.send(mailMessage);
        } catch (Exception e) {
            // Log the error but don't fail if email sending fails
            System.err.println("Failed to send email to " + email + ": " + e.getMessage());
        }
    }

    @Override
    public List<NotificationDto> getUserNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserUserIDOrderByTimestampDesc(userId);
        
        return notifications.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(Long notificationId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }
    
    private NotificationDto convertToDto(Notification notification) {
        NotificationDto dto = new NotificationDto();
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setTimestamp(notification.getTimestamp());
        dto.setRelatedItemId(notification.getRelatedItemId());
        dto.setType(notification.getType());
        return dto;
    }
} 
package tn.esprit.examen.Smartmeet.Services.GhanemRidene;

import tn.esprit.examen.Smartmeet.entities.GhanemRidene.LostAndFound;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.Notification;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.dto.NotificationDto;
import tn.esprit.examen.Smartmeet.entities.Users.Users;

import java.util.List;
import java.util.Set;

public interface NotificationService {
    void notifyEventParticipants(LostAndFound lostAndFound);
    void notifyUser(Users user, String title, String message, Long relatedItemId, String type);
    void sendEmailNotification(String email, String subject, String message);
    List<NotificationDto> getUserNotifications(Long userId);
    void markAsRead(Long notificationId);
} 
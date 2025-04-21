package tn.esprit.examen.Smartmeet.Services.SalmaBenRomdhan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Event;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Notification;
import tn.esprit.examen.Smartmeet.repositories.SalmaBenRomdhan.IEventRepository;
import tn.esprit.examen.Smartmeet.repositories.SalmaBenRomdhan.NotificationRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private IEventRepository eventRepository; // Assurez-vous d'avoir un repository pour accéder aux événements

    public void addEventUpdateNotification(Long eventId) {
        // Vérifier si l'événement existe
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    // Log pour comprendre la cause de l'erreur
                    System.out.println("Erreur: L'événement avec ID " + eventId + " n'a pas été trouvé.");
                    return new RuntimeException("Événement non trouvé pour l'ID : " + eventId);
                });

        // Créer la notification
        Notification notification = new Notification();
        notification.setMessage("L'événement '" + event.getTitle() + "' a été mis à jour.");
        notification.setTimestamp(LocalDateTime.now());
        notification.setEvent(event); // Lier l'événement à la notification

        // Sauvegarder la notification
        notificationRepository.save(notification);

        // Log pour indiquer que la notification a été ajoutée
        System.out.println("Notification ajoutée pour l'événement: " + event.getTitle());
    }


    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }
}
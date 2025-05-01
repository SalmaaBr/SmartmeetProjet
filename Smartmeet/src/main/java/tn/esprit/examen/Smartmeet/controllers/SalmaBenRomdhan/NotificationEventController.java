package tn.esprit.examen.Smartmeet.controllers.SalmaBenRomdhan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.Smartmeet.Services.SalmaBenRomdhan.NotificationService;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Notification;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationEventController {

    @Autowired
    private NotificationService notificationService;



    @PostMapping("/add")
    public ResponseEntity<String> addNotification(@RequestBody Notification notificationRequest) {
        try {
            // Vérifier que l'événement est bien présent dans la notification
            if (notificationRequest.getEvent() == null || notificationRequest.getEvent().getId() == null) {
                // Log détaillé pour déboguer le problème
                System.out.println("Erreur: L'événement ou son ID est manquant dans la notification.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("L'ID de l'événement est manquant ou invalide.");
            }

            Long eventId = notificationRequest.getEvent().getId();

            // Log pour vérifier l'ID de l'événement reçu
            System.out.println("Tentative d'ajout de notification pour l'événement ID: " + eventId);

            // Appel du service pour ajouter une notification
            notificationService.addEventUpdateNotification(eventId);

            return ResponseEntity.status(HttpStatus.CREATED).body("Notification ajoutée avec succès.");
        } catch (Exception e) {
            // Log détaillé pour les erreurs serveur
            System.out.println("Erreur interne lors de l'ajout de la notification: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur interne du serveur : " + e.getMessage());
        }
    }


    @GetMapping
    public List<Notification> getAllNotifications() {
        return notificationService.getAllNotifications();
    }
}
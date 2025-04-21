package tn.esprit.examen.Smartmeet.repositories.SalmaBenRomdhan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Vous pouvez ajouter des méthodes personnalisées si nécessaire
}
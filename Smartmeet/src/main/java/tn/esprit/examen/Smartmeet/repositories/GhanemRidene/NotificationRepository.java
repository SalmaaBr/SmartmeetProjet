package tn.esprit.examen.Smartmeet.repositories.GhanemRidene;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.Notification;
import tn.esprit.examen.Smartmeet.entities.Users.Users;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser(Users user);
    List<Notification> findByUserAndReadFalse(Users user);
    List<Notification> findByUserUserIDOrderByTimestampDesc(Long userID);
}
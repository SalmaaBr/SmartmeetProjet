package tn.esprit.examen.Smartmeet.repositories.SalmaBenRomdhan;


import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Event;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.EventLike;
import tn.esprit.examen.Smartmeet.entities.Users.Users;

import java.util.Optional;

public interface EventLikeRepository extends JpaRepository<EventLike, Long> {
    Optional<EventLike> findByUserAndEvent(Users user, Event event);
    long countByEventAndLikes(Event event, int likes);
}
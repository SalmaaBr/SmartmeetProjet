package tn.esprit.examen.Smartmeet.repositories.GhanemRidene;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.LostAndFound;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.LostAndFoundType;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Event;
import tn.esprit.examen.Smartmeet.entities.Users.Users;

import java.util.List;

@Repository
public interface LostAndFoundRepository extends JpaRepository<LostAndFound, Long> {
    List<LostAndFound> findByEvent(Event event);
    List<LostAndFound> findByEventId(Long eventId);
    List<LostAndFound> findByEventAndType(Event event, LostAndFoundType type);
    List<LostAndFound> findByCreator(Users creator);
} 
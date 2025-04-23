package tn.esprit.examen.Smartmeet.repositories.SalmaBenRomdhan;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Event;

import java.util.Optional;

public interface IEventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByTitle(String title);


}
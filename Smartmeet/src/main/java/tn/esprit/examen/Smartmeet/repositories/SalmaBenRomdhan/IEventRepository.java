package tn.esprit.examen.Smartmeet.repositories.SalmaBenRomdhan;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Event;

public interface IEventRepository extends JpaRepository<Event, Long> {
}
package tn.esprit.examen.Smartmeet.repositories.SalmaBenRomdhan;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.EventUserCalendar;
import tn.esprit.examen.Smartmeet.entities.Users.Users;

import java.util.List;

public interface IEventUserCalendarRepository extends JpaRepository<EventUserCalendar,Long> {
    List<EventUserCalendar> findByUsers(Users users);
}

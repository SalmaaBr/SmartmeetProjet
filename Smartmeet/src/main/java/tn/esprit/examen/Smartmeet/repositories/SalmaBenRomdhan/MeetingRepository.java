package tn.esprit.examen.Smartmeet.repositories.SalmaBenRomdhan;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Meeting;
import tn.esprit.examen.Smartmeet.entities.Users.Users;

import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    List<Meeting> findByOrganizerOrParticipant(Users organizer, Users participant);
}

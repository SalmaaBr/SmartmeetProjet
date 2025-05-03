package tn.esprit.examen.Smartmeet.repositories.MaryemSalhi;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import tn.esprit.examen.Smartmeet.entities.MaryemSalhi.Feedback;

import java.util.List;

public interface IFeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByEventTitle(String eventTitle);
    List<Feedback> findByUserUserID(Long userID);
    @Query("SELECT f FROM Feedback f JOIN FETCH f.user u")
    List<Feedback> findAllWithUser();
    @Query("SELECT f FROM Feedback f JOIN FETCH f.user u WHERE f.eventTitle = :eventTitle")
    List<Feedback> findByEventTitleWithUser(String eventTitle);
}

package tn.esprit.examen.Smartmeet.repositories.MaryemSalhi;

import org.springframework.data.jpa.repository.JpaRepository;

import tn.esprit.examen.Smartmeet.entities.MaryemSalhi.Feedback;

import java.util.List;

public interface IFeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByEventTitle(String eventTitle);
}

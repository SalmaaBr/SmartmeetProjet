package tn.esprit.examen.Smartmeet.repositories.GhanemRiden;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.Engagement;


import java.util.List;

public interface EngagementRepository extends JpaRepository<Engagement, Long> {
  List<Engagement> findByEventId(Long eventId);
}

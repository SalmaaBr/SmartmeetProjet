package tn.esprit.examen.Smartmeet.repositories.SalmaBenRomdhan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Event;

import java.time.LocalDateTime;
import java.util.Optional;

public interface IEventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByTitle(String title);
    @Query("SELECT COUNT(e) > 0 FROM Event e JOIN e.users u WHERE u.userID = :userId " +
            "AND ((e.startTime <= :end AND e.endTime >= :start))")
    boolean existsUserEventsBetween(@Param("userId") Long userId,
                                    @Param("start") LocalDateTime start,
                                    @Param("end") LocalDateTime end);
}


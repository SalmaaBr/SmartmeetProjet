package tn.esprit.examen.Smartmeet.repositories.SalmaBenRomdhan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.EventUserCalendar;
import tn.esprit.examen.Smartmeet.entities.Users.Users;

import java.time.LocalDateTime;
import java.util.List;

public interface IEventUserCalendarRepository extends JpaRepository<EventUserCalendar,Long> {
    List<EventUserCalendar> findByUsers(Users users);
    @Query("SELECT COUNT(c) > 0 FROM EventUserCalendar c WHERE c.users.userID = :userId " +
            "AND ((c.startDate <= :end AND c.endDate >= :start))")
    boolean existsUserCalendarEventsBetween(@Param("userId") Long userId,
                                            @Param("start") LocalDateTime start,
                                            @Param("end") LocalDateTime end);
}

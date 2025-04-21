package tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "eventId")
    private Event event;

    // La méthode getEventId() n'est plus nécessaire, vous pouvez accéder à l'ID via l'entité `Event`.
    public Long getEventId() {
        return event != null ? event.getId() : null;
    }

}
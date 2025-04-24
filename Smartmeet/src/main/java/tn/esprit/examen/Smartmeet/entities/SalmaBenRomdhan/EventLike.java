package tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan;




import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import tn.esprit.examen.Smartmeet.entities.Users.Users;

import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "event_likes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "event_id"})
)
public class EventLike implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference(value = "user-eventLike") // Add back reference for Users
    @JsonIgnoreProperties({"eventLikes", "events", "otherRelations"})
    private Users user;

    @ManyToOne
    @JoinColumn(name = "event_id")
    @JsonBackReference(value = "event-eventLike") // Add back reference for Event
    private Event event;

    private int likes; // 0 for no like, 1 for liked
}
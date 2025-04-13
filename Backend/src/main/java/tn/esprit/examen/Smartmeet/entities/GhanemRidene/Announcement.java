package tn.esprit.examen.Smartmeet.entities.GhanemRidene;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Event;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@Entity
public class Announcement {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;

  private String description;

  private LocalDateTime datePosted;

  @Enumerated(EnumType.STRING)
  private Status status = Status.UNRESOLVED;

  @ManyToOne
  @JoinColumn(name = "event_id")
  Event event;

  @PrePersist
  public void prePersist() {
    this.datePosted = LocalDateTime.now();
  }
}

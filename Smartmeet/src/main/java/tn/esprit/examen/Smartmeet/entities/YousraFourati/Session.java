package tn.esprit.examen.Smartmeet.entities.YousraFourati;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Event;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@Entity

public class Session  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sessionID;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    @Enumerated(EnumType.STRING)
    private TypeSessionStatus sessionStatus;

    @ManyToOne
    Event events;


    @ManyToMany(mappedBy="sessions", cascade = CascadeType.ALL)
    private Set<Tags> tags;
}

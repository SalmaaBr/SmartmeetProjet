package tn.esprit.examen.Smartmeet.entities.MaryemSalhi;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import tn.esprit.examen.Smartmeet.entities.Users.Users;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@Entity

public class MentalHealth implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long IdMentalHealth;

    @Enumerated(EnumType.STRING)
    TypeEmotionalState emotionalState;
    int stressLevel;
    String supportiveMessage;
    int points;
    LocalDateTime lastWellnessSession;
    String gamificationReward;
    String postEventMentalStateReport;
    String mindfulnessExerciseRecommendation;
    Boolean registrationStatus;

    @ManyToOne
    Users user;
}

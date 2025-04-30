package tn.esprit.examen.Smartmeet.entities.MaryemSalhi;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    ResponseMoment responseMoment; // 1. Moment de réponse : Avant, Pendant, Après

    int stressLevel; // 2. Évaluation du niveau de stress (1-5)

    @Enumerated(EnumType.STRING)
    TypeEmotionalState emotionalState; // 3. État émotionnel général

    @Enumerated(EnumType.STRING)
    SupportNeed supportNeed; // 5. Besoin de soutien : Oui/Non

    LocalDateTime submissionDate; // Nouveau champ : Date de soumission du formulaire

    @PrePersist
    public void prePersist() {
        if (submissionDate == null) {
            submissionDate = LocalDateTime.now(); // Date automatique
        }
    }

    @ManyToOne
    @JsonIgnore // Éviter les boucles infinies lors de la sérialisation
    Users user;



}

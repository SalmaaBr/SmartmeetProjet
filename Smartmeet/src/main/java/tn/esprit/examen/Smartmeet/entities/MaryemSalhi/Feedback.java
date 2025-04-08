package tn.esprit.examen.Smartmeet.entities.MaryemSalhi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.ResourceReservation;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Event;

import java.io.Serializable;
import java.time.LocalDate;
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@Entity

public class Feedback implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long idFeedback;
    @Column(name = "message", length = 10000)  // or use TEXT for larger comments
    String message;// Contenu du feedback


    @Column(nullable = false)
    LocalDate date = LocalDate.now(); // Date automatique
    @Enumerated(EnumType.STRING)
    TypeFeeling feeling;
    String eventTitle; // Nouveau champ pour stocker le titre de l'événement

    @JsonIgnore
    @ManyToOne
    Event events;

}

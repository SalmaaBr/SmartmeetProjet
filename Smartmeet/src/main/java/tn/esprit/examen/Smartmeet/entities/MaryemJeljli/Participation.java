package tn.esprit.examen.Smartmeet.entities.MaryemJeljli;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Event;
import tn.esprit.examen.Smartmeet.entities.YousraFourati.SmartMeeting;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)

public class Participation implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    private String qrCodeUrl;
    private LocalDateTime registrationDate;
    @Enumerated(EnumType.STRING)
    private TypeParticipationStatus participationStatus;


    @ManyToOne
    Event events;

    @OneToMany(cascade = CascadeType.ALL, mappedBy="participation")
    private Set<Payment> Payments;


    @ManyToMany(mappedBy="participations", cascade = CascadeType.ALL)
    private Set<SmartMeeting> smartmeetings;

    public void setSmartMeeting(SmartMeeting smartMeeting) {
    }
}

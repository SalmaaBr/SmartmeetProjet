package tn.esprit.examen.Smartmeet.entities.MaryemAbid;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Event;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@Entity
public class Resource {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int idResource;

    private String name;

    @Enumerated(EnumType.STRING)
    private TypeResource typeResource;

    @Enumerated(EnumType.STRING)
    private TypeResourceStatus typeResourceStatus;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "resource_reservation_id")
    private ResourceReservation resourceReservation;

    @ManyToOne
    Event events;



}

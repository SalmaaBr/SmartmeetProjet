package tn.esprit.examen.Smartmeet.entities.MaryemJeljli;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@Entity

public class Payment implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)

    private int paymentID;
    private int amount;
    private LocalDate paymentDate;
    @Enumerated(EnumType.STRING)
    private TypePaymentMethod paymentMethod;
    private TypePaymentStatus paymentStatus;


    @ManyToOne
    Participation participation;
}
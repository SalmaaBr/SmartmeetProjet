package tn.esprit.examen.Smartmeet.entities.MaryemAbid;

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

public class InteractivePublication implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int iPublicationId;
    private String title;
    private String description;
    private TypeIPublicationStatus publicationStatus;
    private TypeIPublicationVisibility publicationVisibility;
    private TypeIPublicationModerationStatus publicationModerationStatus;
    private LocalDateTime publicationDate;
    private int interactionCount;
    private LocalDateTime scheduledPublishTime;
    private double recommendationScore;

    @ManyToOne
    Users user;

}
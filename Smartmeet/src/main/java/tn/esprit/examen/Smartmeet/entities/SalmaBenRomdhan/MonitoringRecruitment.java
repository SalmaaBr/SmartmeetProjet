package tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import tn.esprit.examen.Smartmeet.entities.Users.Users;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@Entity
public class MonitoringRecruitment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private TypeFollowUpStatus status;

    private String title;
    private String description;

    private int quizId;
    private String quizResultsLink;
    private String alGeneratedReportLink;
    private String calendarLink;
    private String meetingLink;
    private boolean result;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Users> users;

    @OneToOne(mappedBy="monitorungrecutement")
    @JsonIgnoreProperties("monitorungrecutement") // Ignore la propriété "monitorungrecutement" lors de la sérialisation de Event

    private Event event;

}
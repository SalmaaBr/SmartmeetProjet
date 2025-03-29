package tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import tn.esprit.examen.Smartmeet.entities.Users.Users;

import java.time.LocalDateTime;
import java.util.Set;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@Entity
public class MonitoringRecruitment {
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

}

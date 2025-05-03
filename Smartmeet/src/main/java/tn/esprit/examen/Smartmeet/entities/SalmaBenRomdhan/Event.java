package tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.Sponsor;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.Resource;
import tn.esprit.examen.Smartmeet.entities.MaryemJeljli.Participation;
import tn.esprit.examen.Smartmeet.entities.MaryemSalhi.Feedback;
import tn.esprit.examen.Smartmeet.entities.YousraFourati.Session;
import tn.esprit.examen.Smartmeet.entities.Users.Users;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@Entity
public class Event implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TypeEvent typeevent;

    @Enumerated(EnumType.STRING)
    private TypeTheme typetheme;

    private String title;
    private String description;
    private String location;

    @Enumerated(EnumType.STRING)
    private TypeWeather typeweather;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private int maxParticipants;

    private String filePath;

    private Double latitude;
    private Double longitude;

    @ManyToMany
    @JsonIgnore
    private Set<Users> users;

    @OneToMany(cascade = CascadeType.ALL, mappedBy="events")
    private Set<Session> Sessions;


    @OneToMany(cascade = CascadeType.ALL, mappedBy="events")
    private Set<Feedback> Feedbacks;

    @OneToMany(cascade = CascadeType.ALL, mappedBy="events")
    private Set<Participation> Participations;

    @OneToMany(cascade = CascadeType.ALL, mappedBy="events")
    private Set<Resource> Resources;

    
    @OneToOne
    @JsonIgnoreProperties("event")
    private MonitoringRecruitment monitorungrecutement;

    @ManyToMany
    @JoinTable(
        name = "sponsor_event",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "sponsor_id")
    )
    @JsonIgnore
    private Set<Sponsor> sponsors;
}

package tn.esprit.examen.Smartmeet.entities.GhanemRidene;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Event;
import tn.esprit.examen.Smartmeet.entities.Users.Users;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Sponsor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSponsor;

    private String nom;
    private String description;
    private String image;

    @Enumerated(EnumType.STRING)
    private NiveauSponsor niveau;

    private Boolean statut;
    private String siteWeb;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "responsible_user_id")
    private Users responsibleUser;

    @ManyToMany
    @JoinTable(
        name = "sponsor_event",
        joinColumns = @JoinColumn(name = "sponsor_id"),
        inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private Set<Event> events;

    @OneToMany(mappedBy = "sponsor", cascade = CascadeType.ALL)
    private Set<Contract> contracts;


}

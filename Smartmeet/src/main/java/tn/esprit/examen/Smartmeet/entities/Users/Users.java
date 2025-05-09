package tn.esprit.examen.Smartmeet.entities.Users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.Claim;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.FoundItem;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.Sponsor;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.InteractivePublication;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.PublicationComment;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.PublicationLike;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.ResourceReservation;
import tn.esprit.examen.Smartmeet.entities.MaryemJeljli.Document;
import tn.esprit.examen.Smartmeet.entities.MaryemJeljli.DocumentLike;
import tn.esprit.examen.Smartmeet.entities.MaryemSalhi.MentalHealth;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Event;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.MonitoringRecruitment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Users implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long userID;
    private String username;
    private String email;
    private String password;
    private String phoneNumber;
    private String address;
    private boolean enabled;
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BlacklistedToken> blacklistedTokens = new HashSet<>();

    // Change ici pour permettre plusieurs rôles
    @ElementCollection(targetClass = TypeUserRole.class)
    @Enumerated(EnumType.STRING)
    private Set<TypeUserRole> userRole;

    public void setRoles(Set<TypeUserRole> userRole) {
        this.userRole = userRole;
    }


    public Users(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy="user")
    private Set<InteractivePublication> InteractivePublications;


    @OneToMany(cascade = CascadeType.ALL, mappedBy="user")
    private Set<MentalHealth> MentalHealths;

    @ManyToMany(mappedBy="users", cascade = CascadeType.ALL)
    private Set<Event> events;

    @ManyToMany(mappedBy="users", cascade = CascadeType.ALL)
    private Set<MonitoringRecruitment> monitoringrecruitments;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy="users")
    private Set<Document> Documents;
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DocumentLike> documentLikes = new HashSet<>();

    @OneToMany(mappedBy = "foundByUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FoundItem> reportedItems = new ArrayList<>();

    @OneToMany(mappedBy = "claimedByUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Claim> claims = new ArrayList<>();
    @OneToMany(mappedBy = "admin")
    private List<Sponsor> sponsorsGeres;
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PublicationComment> comments = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResourceReservation> resourceReservations = new ArrayList<>();
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PublicationLike> publicationLike = new ArrayList<>();

}

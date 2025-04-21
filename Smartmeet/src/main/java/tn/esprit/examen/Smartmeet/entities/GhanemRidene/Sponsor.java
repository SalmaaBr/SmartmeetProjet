package tn.esprit.examen.Smartmeet.entities.GhanemRidene;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import tn.esprit.examen.Smartmeet.entities.Users.Users;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@Entity

public class Sponsor implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String logo;
    private String description;
    @Enumerated(EnumType.STRING)
    private SponsorLevel level;
    private BigDecimal contribution;
    private LocalDate startDate; // Start date of the partnership
    private LocalDate endDate; // End date of the partnership


    @OneToMany(mappedBy = "sponsor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EventSponsor> eventSponsors = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users admin;
}


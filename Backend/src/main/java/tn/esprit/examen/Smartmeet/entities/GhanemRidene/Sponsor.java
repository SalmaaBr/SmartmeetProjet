package tn.esprit.examen.Smartmeet.entities.GhanemRidene;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Event;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
public class Sponsor {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String logo;
  private BigDecimal contributionAmount;

  @Enumerated(EnumType.STRING)
  private PartnershipType partnershipType;

  // Relation many-to-many avec Event
  @ManyToMany
  @JoinTable(
    name = "sponsor_event", // Table de jointure
    joinColumns = @JoinColumn(name = "sponsor_id"), // Colonne pour Sponsor
    inverseJoinColumns = @JoinColumn(name = "event_id") // Colonne pour Event
  )
  private List<Event> events = new ArrayList<>();


}

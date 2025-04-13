package tn.esprit.examen.Smartmeet.entities.GhanemRidene;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Event;
import tn.esprit.examen.Smartmeet.entities.Users.Users;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FoundItem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String photo; // URL or path to the item's photo
    private String location; // Where the item was found
    private LocalDateTime dateTime; // Date and time of discovery
    private String description; // Description of the item
    @Enumerated(EnumType.STRING)
    private ItemStatus status; // Status of the item (Available, Claimed, Returned)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event; // The event where the item was found

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "found_by_user_id")
    private Users foundByUser; // The user who reported the found item

    // Relationship: OneToMany with Claim
    @OneToMany(mappedBy = "foundItem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Claim> claims = new ArrayList<>();
}

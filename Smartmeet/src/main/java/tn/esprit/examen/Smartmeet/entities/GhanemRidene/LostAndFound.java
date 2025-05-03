package tn.esprit.examen.Smartmeet.entities.GhanemRidene;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Event;
import tn.esprit.examen.Smartmeet.entities.Users.Users;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class LostAndFound implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    
    String title;
    
    String description;
    
    @Enumerated(EnumType.STRING)
    LostAndFoundType type;
    
    LocalDateTime createdAt;
    
    String imageUrl;
    
    @ManyToOne
    @JoinColumn(name = "event_id")
    Event event;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    Users creator;
} 
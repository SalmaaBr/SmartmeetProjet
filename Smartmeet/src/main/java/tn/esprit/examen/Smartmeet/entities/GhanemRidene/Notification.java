package tn.esprit.examen.Smartmeet.entities.GhanemRidene;

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
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Notification implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    
    String title;
    
    String message;
    
    LocalDateTime timestamp;
    
    @Column(name = "`read`")
    Boolean read = false;
    
    Long relatedItemId;
    
    String type;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    Users user;
} 
package tn.esprit.examen.Smartmeet.entities.MaryemAbid;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@FieldDefaults(level= AccessLevel.PRIVATE)
@Entity
public class PublicationLike implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int likeId;
    
    LocalDateTime createdAt;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publication_i_publication_id")
    InteractivePublication publication;
    
    @ManyToOne
    Users user;
    
    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
} 
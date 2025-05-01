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
public class PublicationComment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int commentId;
    
    String content;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publication_i_publication_id")
    InteractivePublication publication;
    
    @ManyToOne
    Users user;
    
    @Transient
    boolean isEditing;
    
    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
} 
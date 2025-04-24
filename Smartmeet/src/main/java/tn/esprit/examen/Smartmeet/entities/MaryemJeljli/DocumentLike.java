package tn.esprit.examen.Smartmeet.entities.MaryemJeljli;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "document_likes", uniqueConstraints = @UniqueConstraint(columnNames = {"document_id", "user_id"}))
public class DocumentLike implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int likeId;

    LocalDateTime createdAt;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    Document document;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_userid")
    Users user;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
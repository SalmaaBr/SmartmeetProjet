package tn.esprit.examen.Smartmeet.entities.MaryemJeljli;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import tn.esprit.examen.Smartmeet.entities.Users.Users;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@Entity

public class Document {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;
    private LocalDate createdAt;
    //private String pdfPath;
    //private String imagePath;

    @Enumerated(EnumType.STRING)
    private TypeAccessLevelDocument documentAccessLevel;

    @Enumerated(EnumType.STRING)
    private TypeDocumentTheme documentTheme;

    @Enumerated(EnumType.STRING)
    private TypeDocument documentType;

    @Enumerated(EnumType.STRING)
    private TypeDocumentVisibility documentVisibility;



    @ManyToOne
    @JsonIgnore
    private Users users;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<DocumentLike> documentLikes = new HashSet<>();
    @Transient
    public int getLikesCount() {
        return documentLikes != null ? documentLikes.size() : 0;
    }

    // Méthode utilitaire pour vérifier si un utilisateur a déjà liké
    public boolean isLikedByUser(Users user) {
        if (documentLikes == null || user == null) {
            return false;
        }
        return documentLikes.stream().anyMatch(like -> like.getUser().getUserID().equals(user.getUserID()));
    }

}
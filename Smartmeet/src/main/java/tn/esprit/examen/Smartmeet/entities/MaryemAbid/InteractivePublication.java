package tn.esprit.examen.Smartmeet.entities.MaryemAbid;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import tn.esprit.examen.Smartmeet.entities.Users.Users;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString(exclude = {"comments", "likes"})
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@Entity
public class InteractivePublication implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int iPublicationId;
    private String title;
    private String description;
    private TypeIPublicationStatus publicationStatus;
    private TypeIPublicationVisibility publicationVisibility;
    private TypeIPublicationModerationStatus publicationModerationStatus;
    private LocalDateTime publicationDate;
    private int interactionCount;
    private LocalDateTime scheduledPublishTime;
    private double recommendationScore;

    @ManyToOne
    Users user;
    
    @OneToMany(mappedBy = "publication", cascade = CascadeType.ALL, orphanRemoval = true)
    List<PublicationComment> comments = new ArrayList<>();
    
    @OneToMany(mappedBy = "publication", cascade = CascadeType.ALL, orphanRemoval = true)
    List<PublicationLike> likes = new ArrayList<>();
    
    public int getIpublicationId() {
        return iPublicationId;
    }
    
    public void setIpublicationId(int ipublicationId) {
        this.iPublicationId = ipublicationId;
    }
}
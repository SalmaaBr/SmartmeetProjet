package tn.esprit.examen.Smartmeet.entities.YousraFourati;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@Entity

public class Tags {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int tagID;
    private String name;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Session> sessions;
}

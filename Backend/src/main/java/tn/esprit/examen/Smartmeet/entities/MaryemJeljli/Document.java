package tn.esprit.examen.Smartmeet.entities.MaryemJeljli;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import tn.esprit.examen.Smartmeet.entities.Users.Users;

import java.time.LocalDate;

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
    private LocalDate createdAI;
    @Enumerated(EnumType.STRING)
    private TypeDocument documentType  ;
    private TypeDocumentVisibility documentVisibility;
    private TypeAccessLevelDocument documentAccessLevel;
    private TypeDocumentTheme documentTheme;


    @ManyToOne
    Users users;




}
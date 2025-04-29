package tn.esprit.examen.Smartmeet.repositories.MaryemJeljli;



import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.examen.Smartmeet.entities.MaryemJeljli.Document;
import tn.esprit.examen.Smartmeet.entities.MaryemJeljli.DocumentLike;
import tn.esprit.examen.Smartmeet.entities.Users.Users;

import java.util.Optional;

public interface DocumentLikeRepository extends JpaRepository<DocumentLike, Integer> {
    // Vérifier si un utilisateur a déjà liké un document
    boolean existsByDocumentAndUser(Document document, Users user);


    DocumentLike findByDocumentAndUser(Document document, Users user);
}

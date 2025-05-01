package tn.esprit.examen.Smartmeet.repositories.MaryemAbid;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.InteractivePublication;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.PublicationComment;
import tn.esprit.examen.Smartmeet.entities.Users.Users;

import java.util.List;

@Repository
public interface PublicationCommentRepository extends JpaRepository<PublicationComment, Integer> {
    List<PublicationComment> findByPublication(InteractivePublication publication);
    List<PublicationComment> findByUser(Users user);
} 
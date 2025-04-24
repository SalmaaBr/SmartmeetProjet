package tn.esprit.examen.Smartmeet.repositories.MaryemAbid;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.InteractivePublication;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.PublicationLike;
import tn.esprit.examen.Smartmeet.entities.Users.Users;

import java.util.List;
import java.util.Optional;

@Repository
public interface PublicationLikeRepository extends JpaRepository<PublicationLike, Integer> {
    List<PublicationLike> findByPublication(InteractivePublication publication);
    List<PublicationLike> findByUser(Users user);
    Optional<PublicationLike> findByPublicationAndUser(InteractivePublication publication, Users user);
    int countByPublication(InteractivePublication publication);
} 
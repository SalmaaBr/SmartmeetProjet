package tn.esprit.examen.Smartmeet.repositories.MaryemSalhi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tn.esprit.examen.Smartmeet.entities.MaryemSalhi.MentalHealth;
import tn.esprit.examen.Smartmeet.entities.Users.Users;

import java.util.List;

public interface IMentalhealthRepository extends JpaRepository<MentalHealth,Long> {
    //List<MentalHealth> findByUser(Users user);
    List<MentalHealth> findTop3ByUserUserIDOrderBySubmissionDateDesc(Long userId);

    List<MentalHealth> findByUserUserID(Long userID);
    //List<MentalHealth> getLastThreeSubmissionsByUser(Long userId);
    @Query("SELECT mh FROM MentalHealth mh JOIN FETCH mh.user u")
    List<MentalHealth> findAllMentalHealthWithUser();
}
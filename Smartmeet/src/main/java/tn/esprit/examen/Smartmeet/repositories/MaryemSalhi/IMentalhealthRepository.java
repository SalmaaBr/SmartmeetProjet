package tn.esprit.examen.Smartmeet.repositories.MaryemSalhi;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.examen.Smartmeet.entities.MaryemSalhi.MentalHealth;
import tn.esprit.examen.Smartmeet.entities.Users.Users;

import java.util.List;

public interface IMentalhealthRepository extends JpaRepository<MentalHealth,Long> {
    List<MentalHealth> findByUser(Users user);
}
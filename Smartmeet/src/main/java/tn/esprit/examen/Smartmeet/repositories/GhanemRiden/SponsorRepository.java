package tn.esprit.examen.Smartmeet.repositories.GhanemRiden;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.Sponsor;
import tn.esprit.examen.Smartmeet.entities.Users.Users;

import java.util.List;

public interface SponsorRepository extends JpaRepository<Sponsor, Long> {
    List<Sponsor> findByResponsibleUser(Users responsibleUser);
} 
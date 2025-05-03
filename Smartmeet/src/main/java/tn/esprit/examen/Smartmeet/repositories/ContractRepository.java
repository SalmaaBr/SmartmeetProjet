package tn.esprit.examen.Smartmeet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.Contract;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.Sponsor;

import java.util.List;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    List<Contract> findBySponsor(Sponsor sponsor);
  List<Contract> findByEventId(Long eventId);
}

package tn.esprit.examen.Smartmeet.repositories.MaryemAbid;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.Resource;

import java.util.List;

@Repository
public interface IResourceRepository extends JpaRepository<Resource, Integer> {
    
    /**
     * Find all resources with maintenance enabled
     */
    List<Resource> findByMaintenanceEnabledTrue();
}

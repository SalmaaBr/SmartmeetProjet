package tn.esprit.examen.Smartmeet.repositories.MaryemAbid;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.Resource;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.ResourceReservation;

import java.util.List;

public interface IResourceReservationRepository extends JpaRepository<ResourceReservation, Long> {
    @Query("SELECT r FROM ResourceReservation r WHERE r.resource.idResource = :resourceId")
    List<ResourceReservation> findByResourceId(@Param("resourceId") Integer resourceId);

    List<ResourceReservation> findByResource(Resource resource);
}

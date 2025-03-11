package tn.esprit.examen.Smartmeet.Services.MaryemAbid;

import tn.esprit.examen.Smartmeet.entities.MaryemAbid.ResourceReservation;

import java.util.List;
import java.util.Optional;

public interface IResourceReservationServices {
    ResourceReservation createResourceReservation (ResourceReservation resourceReservation);
    Optional<ResourceReservation> getResourceReservationByID(int id);
    List<ResourceReservation> getAllResourceReservations();
    void deleteResourceReservation(Long id);
    void updateResourceReservation(Long id , ResourceReservation resourceReservation);
    ResourceReservation addResourceReservationAndAssignToResource(ResourceReservation resourceReservation, Integer resourceId);
}

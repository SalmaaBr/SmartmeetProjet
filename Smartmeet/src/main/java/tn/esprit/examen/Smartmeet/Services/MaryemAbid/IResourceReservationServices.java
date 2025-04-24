package tn.esprit.examen.Smartmeet.Services.MaryemAbid;

import tn.esprit.examen.Smartmeet.entities.MaryemAbid.MaintenancePeriod;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.ResourceReservation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IResourceReservationServices {
    ResourceReservation createResourceReservation (ResourceReservation resourceReservation);
    Optional<ResourceReservation> getResourceReservationByID(int id);
    List<ResourceReservation> getAllResourceReservations();
    void deleteResourceReservation(Long id);
    void updateResourceReservation(Long id , ResourceReservation resourceReservation);
    ResourceReservation addResourceReservationAndAssignToResource(ResourceReservation resourceReservation, Integer resourceId);
    List<ResourceReservation> findByResourceId(Integer resourceId);
    
    List<LocalDate> getReservedDatesForResource(Integer resourceId);
    boolean isDateRangeAvailable(Integer resourceId, LocalDate startDate, LocalDate endDate);
    
    // New maintenance-related methods
    List<MaintenancePeriod> getUpcomingMaintenancePeriods(Integer resourceId, int monthsToLookAhead);
    boolean reservationOverlapsWithMaintenance(Long reservationId);
}

package tn.esprit.examen.Smartmeet.Services.MaryemAbid;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.MaintenancePeriod;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.Resource;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.ResourceReservation;
import tn.esprit.examen.Smartmeet.repositories.MaryemAbid.IResourceRepository;
import tn.esprit.examen.Smartmeet.repositories.MaryemAbid.IResourceReservationRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ResourceReservationServicesImpl implements IResourceReservationServices {

    private final IResourceReservationRepository reservationRepository;
    private final IResourceRepository resourceRepository;

    @Override
    public ResourceReservation createResourceReservation(ResourceReservation resourceReservation) {
        log.info("Creating resource reservation: {}", resourceReservation);
        return reservationRepository.save(resourceReservation);
    }

    @Override
    public Optional<ResourceReservation> getResourceReservationByID(int id) {
        log.info("Getting resource reservation by ID: {}", id);
        return reservationRepository.findById((long) id);
    }

    @Override
    public List<ResourceReservation> getAllResourceReservations() {
        log.info("Getting all resource reservations");
        List<ResourceReservation> reservations = reservationRepository.findAll();
        log.info("Found {} reservations", reservations.size());
        return reservations;
    }

    @Override
    @Transactional
    public void deleteResourceReservation(Long id) {
        ResourceReservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Resource reservation not found with ID: " + id));

        // Break relationship with resource
        if (reservation.getResource() != null) {
            Resource resource = reservation.getResource();
            resource.getResourceReservations().remove(reservation);
            resourceRepository.save(resource);
        }

        reservationRepository.delete(reservation);
    }

    @Override
    @Transactional
    public void updateResourceReservation(Long id, ResourceReservation resourceReservation) {
        ResourceReservation existingReservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Resource reservation not found with ID: " + id));

        // Update basic fields
        existingReservation.setStartTime(resourceReservation.getStartTime());
        existingReservation.setEndTime(resourceReservation.getEndTime());
        existingReservation.setUser(resourceReservation.getUser());

        // Update resource relationship
        if (resourceReservation.getResource() != null) {
            // Remove from old resource if exists
            if (existingReservation.getResource() != null) {
                Resource oldResource = existingReservation.getResource();
                oldResource.getResourceReservations().remove(existingReservation);
                resourceRepository.save(oldResource);
            }

            // Add to new resource
            Resource newResource = resourceRepository.findById(resourceReservation.getResource().getIdResource())
                    .orElseThrow(() -> new EntityNotFoundException("Resource not found with ID: " + resourceReservation.getResource().getIdResource()));
            existingReservation.setResource(newResource);
            newResource.getResourceReservations().add(existingReservation);
            resourceRepository.save(newResource);
        }

        reservationRepository.save(existingReservation);
    }

    @Override
    @Transactional
    public ResourceReservation addResourceReservationAndAssignToResource(ResourceReservation resourceReservation, Integer resourceId) {
        log.info("Adding resource reservation and assigning to resource ID: {}", resourceId);
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found with ID: " + resourceId));
        log.info("Found resource: {}", resource);

        // Save the new reservation first to generate an ID
        ResourceReservation savedReservation = reservationRepository.save(resourceReservation);
        log.info("Saved initial reservation: {}", savedReservation);

        // Set up the bidirectional relationship
        savedReservation.setResource(resource);
        resource.getResourceReservations().add(savedReservation);
        log.info("Set up bidirectional relationship");

        // Save both entities to update the relationship
        resource = resourceRepository.save(resource);
        savedReservation = reservationRepository.save(savedReservation);
        log.info("Final saved reservation: {}", savedReservation);
        
        return savedReservation;
    }

    @Override
    public List<LocalDate> getReservedDatesForResource(Integer resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found with ID: " + resourceId));

        return reservationRepository.findByResource(resource).stream()
                .map(reservation -> (ResourceReservation) reservation)
                .flatMap(reservation -> {
                    LocalDate start = reservation.getStartTime();
                    LocalDate end = reservation.getEndTime();
                    return start.datesUntil(end.plusDays(1));
                })
                .collect(Collectors.toList());
    }

    @Override
    public boolean isDateRangeAvailable(Integer resourceId, LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            return false;
        }
        
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found with ID: " + resourceId));
        
        // Check if the requested dates overlap with any maintenance period
        if (resource.isMaintenanceEnabled() && resource.isInMaintenancePeriod(startDate, endDate)) {
            log.info("Requested dates {} to {} overlap with a maintenance period for resource {}", 
                    startDate, endDate, resourceId);
            return false;
        }

        List<LocalDate> reservedDates = getReservedDatesForResource(resourceId);
        return startDate.datesUntil(endDate.plusDays(1))
                .noneMatch(reservedDates::contains);
    }

    @Override
    public List<ResourceReservation> findByResourceId(Integer resourceId) {
        log.info("Finding reservations for resource ID: {}", resourceId);
        List<ResourceReservation> reservations = reservationRepository.findByResourceId(resourceId);
        log.info("Found {} reservations for resource ID: {}", reservations.size(), resourceId);
        for (ResourceReservation res : reservations) {
            log.info("Reservation: id={}, resource={}, startTime={}, endTime={}", 
                    res.getReservationId(), 
                    res.getResource() != null ? res.getResource().getIdResource() : "null",
                    res.getStartTime(),
                    res.getEndTime());
        }
        return reservations;
    }

    /**
     * Get all upcoming maintenance periods for a resource within the next year
     */
    @Override
    public List<MaintenancePeriod> getUpcomingMaintenancePeriods(Integer resourceId, int monthsToLookAhead) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found with ID: " + resourceId));
        
        if (!resource.isMaintenanceEnabled() || resource.getInitialMaintenanceDate() == null) {
            return List.of();
        }
        
        LocalDate now = LocalDate.now();
        LocalDate maxDate = now.plusMonths(monthsToLookAhead);
        List<MaintenancePeriod> periods = new java.util.ArrayList<>();
        
        // Find the first maintenance period that hasn't ended yet
        LocalDate checkDate = resource.getInitialMaintenanceDate();
        while (checkDate.plusDays(resource.getMaintenanceDurationDays()).isBefore(now)) {
            checkDate = checkDate.plusMonths(resource.getMaintenancePeriodMonths());
        }
        
        // Add all periods that start before the max date
        while (checkDate.isBefore(maxDate)) {
            LocalDate maintenanceStart = checkDate;
            LocalDate maintenanceEnd = maintenanceStart.plusDays(resource.getMaintenanceDurationDays() - 1);
            
            periods.add(new MaintenancePeriod(maintenanceStart, maintenanceEnd));
            
            // Move to the next maintenance period
            checkDate = checkDate.plusMonths(resource.getMaintenancePeriodMonths());
        }
        
        return periods;
    }

    /**
     * Check if an existing reservation overlaps with any maintenance period
     */
    @Override
    public boolean reservationOverlapsWithMaintenance(Long reservationId) {
        ResourceReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found with ID: " + reservationId));
        
        Resource resource = reservation.getResource();
        if (resource == null || !resource.isMaintenanceEnabled() || resource.getInitialMaintenanceDate() == null) {
            return false;
        }
        
        return resource.isInMaintenancePeriod(reservation.getStartTime(), reservation.getEndTime());
    }
}

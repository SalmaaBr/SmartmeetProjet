package tn.esprit.examen.Smartmeet.controllers.MaryemAbid;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.Smartmeet.Services.MaryemAbid.IResourceReservationServices;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.ResourceReservation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.TypeResource;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.TypeResourceStatus;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.MaintenancePeriod;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.Resource;
import tn.esprit.examen.Smartmeet.repositories.MaryemAbid.IResourceRepository;
import tn.esprit.examen.Smartmeet.entities.Users.Users;
import java.util.NoSuchElementException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/resource-reservations")
@Slf4j
@RequiredArgsConstructor

@Component("maryemResourceReservationRestController")
public class ResourceReservationRestController {
    private final IResourceReservationServices resourceReservationServices;
    private final IResourceRepository resourceRepository;

    @PostMapping
    public ResponseEntity<ResourceReservation> createResourceReservation(@RequestBody ResourceReservation resourceReservation) {
        return ResponseEntity.ok(resourceReservationServices.createResourceReservation(resourceReservation));
    }

    @PostMapping("/{resourceId}")
    public ResponseEntity<ResourceReservation> createResourceReservationWithResource(
            @PathVariable Integer resourceId,
            @RequestBody ResourceReservation resourceReservation) {
        log.info("Creating reservation for resource ID: {} with data: {}", resourceId, resourceReservation);
        ResourceReservation created = resourceReservationServices.addResourceReservationAndAssignToResource(resourceReservation, resourceId);
        log.info("Created reservation: {}", created);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/reserved-dates/{resourceId}")
    public ResponseEntity<List<String>> getReservedDatesForResource(@PathVariable Integer resourceId) {
        try {
            log.info("Getting reserved dates for resource ID: {}", resourceId);
            List<LocalDate> reservedDates = resourceReservationServices.getReservedDatesForResource(resourceId);
            log.info("Reserved dates for resource {}: {}", resourceId, reservedDates);
            
            // Convert LocalDate objects to String
            List<String> reservedDateStrings = reservedDates.stream()
                    .map(LocalDate::toString)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(reservedDateStrings);
        } catch (Exception e) {
            log.error("Error getting reserved dates for resource {}: {}", resourceId, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResourceReservation> getResourceReservationById(@PathVariable int id) {
        Optional<ResourceReservation> resourceReservation = resourceReservationServices.getResourceReservationByID(id);
        return resourceReservation.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ResourceReservation>> getAllResourceReservations() {
        return ResponseEntity.ok(resourceReservationServices.getAllResourceReservations());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResourceReservation(@PathVariable Long id) {
        resourceReservationServices.deleteResourceReservation(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateResourceReservation(@PathVariable Long id, @RequestBody ResourceReservation resourceReservation) {
        resourceReservationServices.updateResourceReservation(id, resourceReservation);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check-availability")
    public ResponseEntity<Boolean> isDateRangeAvailable(
            @RequestParam Integer resourceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            log.info("Checking availability for resource ID: {}, from: {} to: {}", resourceId, startDate, endDate);
            boolean isAvailable = resourceReservationServices.isDateRangeAvailable(resourceId, startDate, endDate);
            log.info("Date range available: {}", isAvailable);
            return ResponseEntity.ok(isAvailable);
        } catch (Exception e) {
            log.error("Error checking date range availability: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/types")
    public ResponseEntity<List<String>> getResourceTypes() {
        return ResponseEntity.ok(Arrays.stream(TypeResource.values())
                .map(Enum::name)
                .collect(Collectors.toList()));
    }

    @GetMapping("/statuses")
    public ResponseEntity<List<String>> getResourceStatuses() {
        return ResponseEntity.ok(Arrays.stream(TypeResourceStatus.values())
                .map(Enum::name)
                .collect(Collectors.toList()));
    }

    @GetMapping("/maintenance-periods/{resourceId}")
    public ResponseEntity<List<MaintenancePeriod>> getUpcomingMaintenancePeriods(
            @PathVariable Integer resourceId,
            @RequestParam(defaultValue = "12") int months) {
        try {
            log.info("Getting upcoming maintenance periods for resource ID: {}, months: {}", resourceId, months);
            List<MaintenancePeriod> periods = resourceReservationServices.getUpcomingMaintenancePeriods(resourceId, months);
            log.info("Found {} maintenance periods", periods.size());
            return ResponseEntity.ok(periods);
        } catch (Exception e) {
            log.error("Error getting maintenance periods for resource {}: {}", resourceId, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/check-maintenance-overlap/{reservationId}")
    public ResponseEntity<Boolean> checkMaintenanceOverlap(@PathVariable Long reservationId) {
        try {
            log.info("Checking if reservation {} overlaps with maintenance", reservationId);
            boolean overlaps = resourceReservationServices.reservationOverlapsWithMaintenance(reservationId);
            log.info("Reservation {} overlaps with maintenance: {}", reservationId, overlaps);
            return ResponseEntity.ok(overlaps);
        } catch (Exception e) {
            log.error("Error checking maintenance overlap for reservation {}: {}", reservationId, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/conflicting-reservations/{resourceId}")
    public ResponseEntity<?> getConflictingReservations(@PathVariable Long resourceId) {
        try {
            log.info("Get conflicting reservations for resource with ID: {}", resourceId);
            
            // Get the resource
            Resource resource = resourceRepository.findById(Math.toIntExact(resourceId))
                    .orElseThrow(() -> new NoSuchElementException("Resource not found with ID: " + resourceId));
            
            // Check if maintenance is enabled
            if (!resource.isMaintenanceEnabled()) {
                log.info("Maintenance is not enabled for resource ID: {}", resourceId);
                return ResponseEntity.ok(Collections.emptyList());
            }
            
            // Get maintenance periods for the next year
            LocalDate now = LocalDate.now();
            LocalDate oneYearFromNow = now.plusYears(1);
            List<LocalDate> maintenanceDates = new ArrayList<>();
            
            // Generate maintenance dates based on resource configuration
            if (resource.getMaintenancePeriodMonths() > 0 && resource.getMaintenanceDurationDays() > 0) {
                LocalDate nextMaintenance = resource.getInitialMaintenanceDate();
                while (nextMaintenance.isBefore(oneYearFromNow)) {
                    LocalDate endDate = nextMaintenance.plusDays(resource.getMaintenanceDurationDays());
                    
                    // Add all dates in the maintenance period
                    LocalDate currentDate = nextMaintenance;
                    while (!currentDate.isAfter(endDate)) {
                        maintenanceDates.add(currentDate);
                        currentDate = currentDate.plusDays(1);
                    }
                    
                    // Calculate next maintenance date
                    nextMaintenance = nextMaintenance.plusMonths(resource.getMaintenancePeriodMonths());
                }
            }
            
            if (maintenanceDates.isEmpty()) {
                log.info("No maintenance dates found for resource ID: {}", resourceId);
                return ResponseEntity.ok(Collections.emptyList());
            }
            
            // Get all reservations for this resource
            List<ResourceReservation> reservations = resourceReservationServices.findByResourceId(Math.toIntExact(resourceId));
            
            // Find reservations that overlap with maintenance periods
            List<Map<String, Object>> conflictingReservations = new ArrayList<>();
            
            for (ResourceReservation reservation : reservations) {
                LocalDate startDate = reservation.getStartTime();
                LocalDate endDate = reservation.getEndTime();
                
                // Check if any day of the reservation falls within a maintenance period
                LocalDate currentDate = startDate;
                boolean conflicts = false;
                
                while (!currentDate.isAfter(endDate)) {
                    if (maintenanceDates.contains(currentDate)) {
                        conflicts = true;
                        break;
                    }
                    currentDate = currentDate.plusDays(1);
                }
                
                if (conflicts) {
                    Users user = reservation.getUser();
                    Map<String, Object> reservationDetails = new HashMap<>();
                    reservationDetails.put("reservationId", reservation.getReservationId());
                    reservationDetails.put("userId", user.getUserID());
                    reservationDetails.put("userName", user.getUsername());
                    reservationDetails.put("startTime", reservation.getStartTime());
                    reservationDetails.put("endTime", reservation.getEndTime());
                    reservationDetails.put("resourceId", resourceId);
                    
                    conflictingReservations.add(reservationDetails);
                }
            }
            
            log.info("Found {} conflicting reservations for resource ID: {}", conflictingReservations.size(), resourceId);
            return ResponseEntity.ok(conflictingReservations);
            
        } catch (Exception e) {
            log.error("Error getting conflicting reservations: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error getting conflicting reservations: " + e.getMessage());
        }
    }
} 
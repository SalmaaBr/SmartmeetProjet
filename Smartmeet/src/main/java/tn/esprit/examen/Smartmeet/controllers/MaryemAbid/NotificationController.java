package tn.esprit.examen.Smartmeet.controllers.MaryemAbid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.Smartmeet.Services.MaryemAbid.IResourceReservationServices;
import tn.esprit.examen.Smartmeet.email.EmailService;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.MaintenanceNotification;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.MaintenancePeriod;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.Resource;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.ResourceReservation;
import tn.esprit.examen.Smartmeet.entities.Users.Users;
import tn.esprit.examen.Smartmeet.repositories.MaryemAbid.IMaintenanceNotificationRepository;
import tn.esprit.examen.Smartmeet.repositories.MaryemAbid.IResourceRepository;
import tn.esprit.examen.Smartmeet.repositories.MaryemAbid.IResourceReservationRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@Slf4j
@RequiredArgsConstructor
public class NotificationController {
    
    private final IResourceReservationServices resourceReservationServices;
    private final IResourceRepository resourceRepository;
    private final IResourceReservationRepository reservationRepository;
    private final IMaintenanceNotificationRepository notificationRepository;
    private final EmailService emailService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MaintenanceNotification>> getUserNotifications(@PathVariable Long userId) {
        try {
            log.info("Getting notifications for user ID: {}", userId);
            List<MaintenanceNotification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            log.error("Error getting notifications for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<List<MaintenanceNotification>> getUserNotificationsByUsername(@PathVariable String username) {
        try {
            log.info("Getting notifications for username: {}", username);
            List<MaintenanceNotification> notifications = notificationRepository.findByUsernameOrderByCreatedAtDesc(username);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            log.error("Error getting notifications for username {}: {}", username, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Integer> getUnreadCount(@PathVariable Long userId) {
        try {
            int count = notificationRepository.countByUserIdAndStatus(userId, "PENDING");
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("Error getting unread count for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/username/{username}/unread-count")
    public ResponseEntity<Integer> getUnreadCountByUsername(@PathVariable String username) {
        try {
            log.info("Getting unread count for username: {}", username);
            int count = notificationRepository.countByUsernameAndStatus(username, "PENDING");
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("Error getting unread count for username {}: {}", username, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<MaintenanceNotification> markAsRead(@PathVariable Long notificationId) {
        try {
            Optional<MaintenanceNotification> notification = notificationRepository.findById(notificationId);
            if (notification.isPresent()) {
                MaintenanceNotification entity = notification.get();
                entity.setStatus("READ");
                notificationRepository.save(entity);
                return ResponseEntity.ok(entity);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error marking notification {} as read: {}", notificationId, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/maintenance")
    public ResponseEntity<MaintenanceNotification> createMaintenanceNotification(@RequestBody MaintenanceNotification notification) {
        try {
            notification.setCreatedAt(LocalDate.now());
            if (notification.getStatus() == null) {
                notification.setStatus("PENDING");
            }
            
            MaintenanceNotification saved = notificationRepository.save(notification);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            log.error("Error creating maintenance notification: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/maintenance/send-upcoming/{resourceId}")
    public ResponseEntity<Integer> sendUpcomingMaintenanceNotifications(
            @PathVariable Integer resourceId,
            @RequestParam(defaultValue = "7") int days) {
        try {
            log.info("Sending notifications for upcoming maintenance for resource {}, {} days in advance", resourceId, days);
            
            Resource resource = resourceRepository.findById(resourceId)
                    .orElseThrow(() -> new NoSuchElementException("Resource not found with ID: " + resourceId));
            
            if (!resource.isMaintenanceEnabled() || resource.getInitialMaintenanceDate() == null) {
                return ResponseEntity.ok(0); // No maintenance configured
            }
            
            // Get upcoming maintenance periods
            List<MaintenancePeriod> periods = resourceReservationServices.getUpcomingMaintenancePeriods(resourceId, 3);
            LocalDate now = LocalDate.now();
            
            // Filter for periods that are within the specified days range
            List<MaintenancePeriod> upcomingPeriods = periods.stream()
                    .filter(p -> {
                        LocalDate start = p.getStartDate();
                        long daysUntil = ChronoUnit.DAYS.between(now, start);
                        return daysUntil >= 0 && daysUntil <= days;
                    })
                    .collect(Collectors.toList());
            
            if (upcomingPeriods.isEmpty()) {
                log.info("No upcoming maintenance periods found within {} days", days);
                return ResponseEntity.ok(0);
            }
            
            int notificationsSent = 0;
            
            // For each upcoming period, find affected reservations and notify users
            for (MaintenancePeriod period : upcomingPeriods) {
                LocalDate maintenanceStart = period.getStartDate();
                LocalDate maintenanceEnd = period.getEndDate();
                
                List<ResourceReservation> affectedReservations = findAffectedReservations(resource, maintenanceStart, maintenanceEnd);
                
                log.info("Found {} reservations affected by maintenance from {} to {}", 
                        affectedReservations.size(), period.getStartDate(), period.getEndDate());
                
                // Create notifications for each affected reservation
                for (ResourceReservation reservation : affectedReservations) {
                    Users user = reservation.getUser();
                    if (user != null) {
                        // Check if notification already exists
                        boolean exists = notificationRepository.existsByUserIdAndReservationIdAndMaintenanceStartDate(
                                user.getUserID(), reservation.getReservationId(), maintenanceStart.toString());
                        
                        if (!exists) {
                            // Create notification
                            MaintenanceNotification notification = new MaintenanceNotification();
                            notification.setUserId(user.getUserID());
                            notification.setResourceId(resourceId);
                            notification.setResourceName(resource.getName());
                            notification.setReservationId(reservation.getReservationId());
                            notification.setStartDate(reservation.getStartTime().toString());
                            notification.setEndDate(reservation.getEndTime().toString());
                            notification.setMaintenanceStartDate(maintenanceStart.toString());
                            notification.setMaintenanceEndDate(maintenanceEnd.toString());
                            notification.setStatus("PENDING");
                            notification.setCreatedAt(LocalDate.now());
                            
                            notificationRepository.save(notification);
                            notificationsSent++;
                            
                            // Send email notification
                            try {
                                String email = user.getEmail();
                                String subject = "Maintenance Notification for " + resource.getName();
                                String message = String.format(
                                        "Dear %s,\n\nWe wanted to inform you that there is scheduled maintenance for %s " +
                                        "from %s to %s.\n\n" +
                                        "Your reservation from %s to %s may be affected.\n\n" +
                                        "Please contact the administrator if you need to reschedule your reservation.\n\n" +
                                        "Regards,\nThe SmartMeet Team",
                                        user.getUsername(),
                                        resource.getName(),
                                        maintenanceStart,
                                        maintenanceEnd,
                                        reservation.getStartTime(),
                                        reservation.getEndTime()
                                );
                                
                                emailService.sendSimpleMessage(email, subject, message);
                                log.info("Email notification sent to {}", email);
                            } catch (Exception e) {
                                log.error("Failed to send email notification: {}", e.getMessage(), e);
                            }
                        }
                    }
                }
            }
            
            return ResponseEntity.ok(notificationsSent);
            
        } catch (Exception e) {
            log.error("Error sending maintenance notifications: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/maintenance/affected-reservations/{resourceId}")
    public ResponseEntity<List<Map<String, Object>>> getAffectedReservations(
            @PathVariable Integer resourceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            log.info("Finding reservations affected by maintenance from {} to {} for resource {}", startDate, endDate, resourceId);
            
            Resource resource = resourceRepository.findById(resourceId)
                    .orElseThrow(() -> new NoSuchElementException("Resource not found with ID: " + resourceId));
            
            List<ResourceReservation> affectedReservations = findAffectedReservations(resource, startDate, endDate);
            
            // Convert to response format
            List<Map<String, Object>> response = affectedReservations.stream()
                    .map(reservation -> {
                        Map<String, Object> data = new HashMap<>();
                        data.put("reservationId", reservation.getReservationId());
                        
                        Users user = reservation.getUser();
                        if (user != null) {
                            data.put("userId", user.getUserID());
                            data.put("userName", user.getUsername());
                            data.put("userEmail", user.getEmail());
                        }
                        
                        data.put("startDate", reservation.getStartTime().toString());
                        data.put("endDate", reservation.getEndTime().toString());
                        
                        return data;
                    })
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error finding affected reservations: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    private List<ResourceReservation> findAffectedReservations(Resource resource, LocalDate maintenanceStart, LocalDate maintenanceEnd) {
        List<ResourceReservation> allReservations = reservationRepository.findByResource(resource);
        
        return allReservations.stream()
                .filter(reservation -> {
                    LocalDate reservationStart = reservation.getStartTime();
                    LocalDate reservationEnd = reservation.getEndTime();
                    
                    // Check if there's an overlap
                    return !(reservationEnd.isBefore(maintenanceStart) || reservationStart.isAfter(maintenanceEnd));
                })
                .collect(Collectors.toList());
    }

    /**
     * Admin endpoint to manually trigger maintenance notifications for all resources
     * @param days Number of days in advance to check for maintenance (default: 7)
     * @return Total number of notifications sent
     */
    @PostMapping("/maintenance/send-all")
    public ResponseEntity<Integer> sendAllMaintenanceNotifications(
            @RequestParam(defaultValue = "7") int days) {
        try {
            log.info("Manually triggering maintenance notifications for all resources, {} days in advance", days);
            
            // Get all resources with maintenance enabled
            List<Resource> resources = resourceRepository.findByMaintenanceEnabledTrue();
            log.info("Found {} resources with maintenance enabled", resources.size());
            
            int totalNotificationsSent = 0;
            
            // For each resource, send notifications
            for (Resource resource : resources) {
                try {
                    ResponseEntity<Integer> response = sendUpcomingMaintenanceNotifications(
                            resource.getIdResource(), days);
                    
                    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                        totalNotificationsSent += response.getBody();
                    }
                } catch (Exception e) {
                    log.error("Error sending notifications for resource {}: {}", 
                            resource.getIdResource(), e.getMessage(), e);
                    // Continue with other resources even if one fails
                }
            }
            
            log.info("Manual notification trigger completed. Total notifications sent: {}", totalNotificationsSent);
            return ResponseEntity.ok(totalNotificationsSent);
            
        } catch (Exception e) {
            log.error("Error sending all maintenance notifications: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
} 
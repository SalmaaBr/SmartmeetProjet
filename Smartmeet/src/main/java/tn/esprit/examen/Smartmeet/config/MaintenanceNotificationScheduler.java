package tn.esprit.examen.Smartmeet.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tn.esprit.examen.Smartmeet.controllers.MaryemAbid.NotificationController;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.Resource;
import tn.esprit.examen.Smartmeet.repositories.MaryemAbid.IResourceRepository;

import java.util.List;

/**
 * This scheduler automatically sends notifications for upcoming maintenance
 * on all enabled resources at regular intervals.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class MaintenanceNotificationScheduler {

    private final NotificationController notificationController;
    private final IResourceRepository resourceRepository;
    
    @Value("${maintenance.notification.enabled:true}")
    private boolean notificationsEnabled;
    
    @Value("${maintenance.notification.days-in-advance:7}")
    private int daysInAdvance;

    /**
     * Scheduled task that runs based on the configured cron expression
     * Uses cron expression from properties: second, minute, hour, day of month, month, day of week
     */
    @Scheduled(cron = "${maintenance.notification.scheduler.cron:0 0 1 * * ?}")
    public void sendMaintenanceNotifications() {
        if (!notificationsEnabled) {
            log.info("Maintenance notifications are disabled. Skipping scheduled check.");
            return;
        }
        
        log.info("Starting scheduled maintenance notification task, checking {} days in advance", daysInAdvance);
        
        try {
            // Get all resources with maintenance enabled
            List<Resource> resources = resourceRepository.findByMaintenanceEnabledTrue();
            log.info("Found {} resources with maintenance enabled", resources.size());
            
            int totalNotificationsSent = 0;
            
            // For each resource, send notifications
            for (Resource resource : resources) {
                try {
                    ResponseEntity<Integer> response = notificationController.sendUpcomingMaintenanceNotifications(
                            resource.getIdResource(), daysInAdvance);
                    
                    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                        int notificationsSent = response.getBody();
                        totalNotificationsSent += notificationsSent;
                        log.info("Sent {} notifications for resource: {} (ID: {})", 
                                notificationsSent, resource.getName(), resource.getIdResource());
                    }
                } catch (Exception e) {
                    log.error("Error sending notifications for resource {}: {}", 
                            resource.getIdResource(), e.getMessage(), e);
                    // Continue with other resources even if one fails
                }
            }
            
            log.info("Maintenance notification task completed. Total notifications sent: {}", totalNotificationsSent);
            
        } catch (Exception e) {
            log.error("Error in maintenance notification scheduler: {}", e.getMessage(), e);
        }
    }
} 
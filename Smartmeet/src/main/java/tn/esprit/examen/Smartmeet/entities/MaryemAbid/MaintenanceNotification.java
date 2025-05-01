package tn.esprit.examen.Smartmeet.entities.MaryemAbid;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class MaintenanceNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    
    // User who should receive the notification
    Long userId;
    
    // Resource details
    Integer resourceId;
    String resourceName;
    
    // Reservation details (if applicable)
    Integer reservationId;
    
    // Dates related to the reservation
    String startDate;
    String endDate;
    
    // Dates related to the maintenance
    String maintenanceStartDate;
    String maintenanceEndDate;
    
    // Status of the notification (PENDING, SENT, READ)
    String status;
    
    // When the notification was created
    LocalDate createdAt;
    
    @Override
    public String toString() {
        return "MaintenanceNotification{" +
                "id=" + id +
                ", userId=" + userId +
                ", resourceId=" + resourceId +
                ", resourceName='" + resourceName + '\'' +
                ", reservationId=" + reservationId +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", maintenanceStartDate='" + maintenanceStartDate + '\'' +
                ", maintenanceEndDate='" + maintenanceEndDate + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
} 
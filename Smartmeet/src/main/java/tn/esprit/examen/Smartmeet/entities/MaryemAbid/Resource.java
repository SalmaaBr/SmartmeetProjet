package tn.esprit.examen.Smartmeet.entities.MaryemAbid;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Event;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@Entity
public class Resource {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int idResource;

    private String name;

    @Enumerated(EnumType.STRING)
    private TypeResource typeResource;

    @Enumerated(EnumType.STRING)
    private TypeResourceStatus typeResourceStatus;
    
    // Maintenance-related fields
    private boolean maintenanceEnabled = false;
    
    // Period between maintenance (stored in months)
    private int maintenancePeriodMonths = 6;
    
    // Duration of maintenance in days
    private int maintenanceDurationDays = 1;
    
    // Date of the first maintenance
    private LocalDate initialMaintenanceDate;
    
    // Date of the next scheduled maintenance
    private LocalDate nextMaintenanceDate;

    @JsonIgnore
    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResourceReservation> resourceReservations = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event events;
    
    /**
     * Calculate the next maintenance date based on the maintenance period
     */
    @PrePersist
    @PreUpdate
    public void calculateNextMaintenanceDate() {
        if (maintenanceEnabled && initialMaintenanceDate != null) {
            LocalDate now = LocalDate.now();
            LocalDate baseDate = initialMaintenanceDate;
            
            // If the initial date is in the past, calculate the next upcoming maintenance
            while (baseDate.isBefore(now)) {
                baseDate = baseDate.plusMonths(maintenancePeriodMonths);
            }
            
            this.nextMaintenanceDate = baseDate;
        }
    }
    
    /**
     * Check if a date range overlaps with any maintenance period
     */
    public boolean isInMaintenancePeriod(LocalDate startDate, LocalDate endDate) {
        if (!maintenanceEnabled || initialMaintenanceDate == null) {
            return false;
        }
        
        // Calculate all maintenance periods that could overlap with the given date range
        LocalDate checkDate = initialMaintenanceDate;
        LocalDate maxCheckDate = endDate.plusMonths(maintenancePeriodMonths); // Look ahead enough to catch any overlap
        
        while (checkDate.isBefore(maxCheckDate)) {
            LocalDate maintenanceStart = checkDate;
            LocalDate maintenanceEnd = maintenanceStart.plusDays(maintenanceDurationDays - 1);
            
            // Check if this maintenance period overlaps with the requested dates
            if (!(endDate.isBefore(maintenanceStart) || startDate.isAfter(maintenanceEnd))) {
                return true; // Overlap found
            }
            
            // Move to the next maintenance period
            checkDate = checkDate.plusMonths(maintenancePeriodMonths);
        }
        
        return false; // No overlap found
    }

    /**
     * Custom toString method to prevent infinite recursion
     */
    @Override
    public String toString() {
        return "Resource{" +
                "idResource=" + idResource +
                ", name='" + name + '\'' +
                ", typeResource=" + typeResource +
                ", typeResourceStatus=" + typeResourceStatus +
                ", maintenanceEnabled=" + maintenanceEnabled +
                ", maintenancePeriodMonths=" + maintenancePeriodMonths +
                ", maintenanceDurationDays=" + maintenanceDurationDays +
                ", initialMaintenanceDate=" + initialMaintenanceDate +
                ", nextMaintenanceDate=" + nextMaintenanceDate +
                '}';
    }
}

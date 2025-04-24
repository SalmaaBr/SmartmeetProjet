package tn.esprit.examen.Smartmeet.entities.MaryemAbid;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import tn.esprit.examen.Smartmeet.entities.Users.Users;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@Entity
public class ResourceReservation implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int reservationId;

    private LocalDate startTime;
    private LocalDate endTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "resource_id")
    private Resource resource;

    @PrePersist
    public void validateDates() {
        if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time cannot be after end time");
        }
    }
    
    /**
     * Custom toString method to prevent infinite recursion
     */
    @Override
    public String toString() {
        return "ResourceReservation{" +
                "reservationId=" + reservationId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", user=" + (user != null ? user.getUserID() : null) +
                ", createdAt=" + createdAt +
                ", resourceId=" + (resource != null ? resource.getIdResource() : null) +
                '}';
    }
}

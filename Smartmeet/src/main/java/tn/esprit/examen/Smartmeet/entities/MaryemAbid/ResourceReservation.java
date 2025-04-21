package tn.esprit.examen.Smartmeet.entities.MaryemAbid;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
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

    @Column(nullable = true)  // Nullable set to true, in case the user is optional
    private String user;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "resourceReservation")
    private List<Resource> resources;

    @PrePersist
    public void validateDates() {
        if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time cannot be after end time");
        }
    }
}

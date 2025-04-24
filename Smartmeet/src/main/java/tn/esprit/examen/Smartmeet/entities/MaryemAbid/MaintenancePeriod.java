package tn.esprit.examen.Smartmeet.entities.MaryemAbid;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Class representing a maintenance period with start and end dates
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaintenancePeriod {
    private LocalDate startDate;
    private LocalDate endDate;
} 
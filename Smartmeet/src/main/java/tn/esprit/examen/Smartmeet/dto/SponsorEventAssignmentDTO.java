package tn.esprit.examen.Smartmeet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SponsorEventAssignmentDTO {
    private Long sponsorId;
    private Long eventId;
    private String terms;
    private Double amount;
    private String expiryDate;
} 
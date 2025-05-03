package tn.esprit.examen.Smartmeet.entities.GhanemRidene.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.LostAndFoundType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LostAndFoundRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotNull(message = "Type is required")
    private LostAndFoundType type;
    
    private String imageUrl;
    
    @NotNull(message = "Event ID is required")
    private Long eventId;
} 
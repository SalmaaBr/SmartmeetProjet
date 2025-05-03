package tn.esprit.examen.Smartmeet.entities.GhanemRidene.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.LostAndFoundType;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LostAndFoundResponse {
    private Long id;
    private String title;
    private String description;
    private LostAndFoundType type;
    private LocalDateTime createdAt;
    private String imageUrl;
    private Long eventId;
    private String eventTitle;
    private Long creatorId;
    private String creatorUsername;
} 
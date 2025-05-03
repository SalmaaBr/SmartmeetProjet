package tn.esprit.examen.Smartmeet.entities.GhanemRidene.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto {
    private String title;
    private String message;
    private LocalDateTime timestamp;
    private boolean read;
    private Long relatedItemId;
    private String type;
} 
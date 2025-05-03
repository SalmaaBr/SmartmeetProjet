package tn.esprit.examen.Smartmeet.dto.MaryemSalhi;

import lombok.Data;

import java.util.Map;

@Data
public class FeedbackStats {
    private long totalFeedbacks;
    private String eventTitle; // Optionnel, pour les statistiques par événement
    private Map<String, Long> sentimentCounts; // Nombre de feedbacks par sentiment
    private Map<String, Double> sentimentPercentages; // Pourcentage de chaque sentiment

}

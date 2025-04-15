package tn.esprit.examen.Smartmeet.dto.MaryemSalhi;

import java.util.Map;

public class FeedbackStats {

    private Map<String, Double> averageFeelingByEvent; // Ex. : {"Conférence IA 2025": 4.2, "Workshop ML": 3.8}

    // Constructeurs
    public FeedbackStats() {}
    public FeedbackStats(Map<String, Double> averageFeelingByEvent) {
        this.averageFeelingByEvent = averageFeelingByEvent;
    }

    // Getters et Setters
    public Map<String, Double> getAverageFeelingByEvent() {
        return averageFeelingByEvent;
    }

    public void setAverageFeelingByEvent(Map<String, Double> averageFeelingByEvent) {
        this.averageFeelingByEvent = averageFeelingByEvent;
    }
}

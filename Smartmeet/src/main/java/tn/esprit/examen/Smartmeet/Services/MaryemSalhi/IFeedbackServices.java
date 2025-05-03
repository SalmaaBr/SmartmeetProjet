package tn.esprit.examen.Smartmeet.Services.MaryemSalhi;


import tn.esprit.examen.Smartmeet.dto.MaryemSalhi.FeedbackStats;
import tn.esprit.examen.Smartmeet.entities.MaryemSalhi.Feedback;

import java.util.List;

public interface IFeedbackServices {
    Feedback addFeedback(Feedback feedBack);
    Feedback  updateFeedback(Feedback  feedBack);

    Feedback updateFeedbackWithEvent(Feedback feedback, Long eventId);

    void deleteFeedback(Integer id);
    Feedback getFeedbackById(Integer id);
    List<Feedback > getAllFeedbacks();
    Feedback addFeedbackAndAffectToEvents(Feedback feedback, Long eventId);
    List<Feedback> getFeedbacksByEventTitle(String eventTitle); // Nouvelle méthode
    FeedbackStats getSentimentStatistics(String eventTitle); // Nouvelle méthode
    //FeedbackStats getFeedbackStats(String eventTitle); // Nouvelle méthode
    List<Feedback> getFeedbackByUserId(Long userId); // New method

    Feedback updateFeedbackWithEventAndUser(Feedback feedback, Long eventId, Long userId);
}

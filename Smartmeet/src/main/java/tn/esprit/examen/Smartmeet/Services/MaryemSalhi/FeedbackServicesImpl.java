package tn.esprit.examen.Smartmeet.Services.MaryemSalhi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.examen.Smartmeet.dto.MaryemSalhi.FeedbackStats;
import tn.esprit.examen.Smartmeet.entities.MaryemSalhi.Feedback;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Event;
import tn.esprit.examen.Smartmeet.repositories.MaryemSalhi.IFeedbackRepository;
import tn.esprit.examen.Smartmeet.repositories.SalmaBenRomdhan.IEventRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service

public class FeedbackServicesImpl implements IFeedbackServices {

    private final IFeedbackRepository feedbackRepository;
    private final IEventRepository eventRepository;
    private final GeminiService geminiService;
    private final SentimentAnalysisService sentimentAnalysisService; // Nouveau service

    @Override
    public Feedback addFeedback(Feedback feedback) {
        log.info("Adding feedback: {}", feedback);


        // Analyser le sentiment
        try {
            String sentiment = sentimentAnalysisService.analyzeSentiment(feedback.getMessage());
            feedback.setSentiment(sentiment);
            log.info("Sentiment détecté: {}", sentiment);
        } catch (Exception e) {
            log.error("Erreur lors de l'analyse du sentiment: {}", e.getMessage());
            feedback.setSentiment("NEUTRAL");
        }

        // Reformuler le message s'il n'est pas vide
        if (feedback.getMessage() != null && !feedback.getMessage().isBlank()) {
            String professionalMessage = geminiService.reformulateMessage(feedback.getMessage());
            feedback.setMessage(professionalMessage); // mise à jour du message
        }

        // Remplir le titre de l’événement si nécessaire
        if (feedback.getEvents() != null) {
            feedback.setEventTitle(feedback.getEvents().getTitle());
        }

        return feedbackRepository.save(feedback);
    }


    @Override
    public Feedback updateFeedback(Feedback feedback) {
        log.info("Updating feedback: {}", feedback);
        // Si un événement est associé, mettre à jour eventTitle
        if (feedback.getEvents() != null) {
            feedback.setEventTitle(feedback.getEvents().getTitle());
        }
        return feedbackRepository.save(feedback);
    }

    @Override
    public Feedback updateFeedbackWithEvent(Feedback feedback, Long eventId) {
        log.info("Updating feedback with ID: {} and event ID: {}", feedback.getIdFeedback(), eventId);
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isPresent()) {
            feedback.setEvents(event.get());
            feedback.setEventTitle(event.get().getTitle());
            return feedbackRepository.save(feedback);
        } else {
            log.warn("Event with ID: {} not found", eventId);
            throw new IllegalArgumentException("Event with ID " + eventId + " not found");
        }
    }

    @Override
    public void deleteFeedback(Integer id) {
        log.info("Deleting feedback with ID: {}", id);
        feedbackRepository.deleteById(Long.valueOf(id));
    }

    @Override
    public Feedback getFeedbackById(Integer id) {
        log.info("Fetching feedback with ID: {}", id);
        Optional<Feedback> feedbackOpt = feedbackRepository.findById(Long.valueOf(id));
        Feedback feedback = feedbackOpt.orElse(null);
        if (feedback != null && feedback.getEventTitle() == null && feedback.getEvents() != null) {
            feedback.setEventTitle(feedback.getEvents().getTitle());
            log.info("Populated eventTitle for feedback ID {}: {}", feedback.getIdFeedback(), feedback.getEventTitle());
        }
        return feedback;
    }

    @Override
    public List<Feedback> getAllFeedbacks() {
        log.info("Fetching all feedbacks");
        List<Feedback> feedbacks = feedbackRepository.findAll();
        feedbacks.forEach(feedback -> {
            if (feedback.getEventTitle() == null && feedback.getEvents() != null) {
                feedback.setEventTitle(feedback.getEvents().getTitle());
                log.info("Populated eventTitle for feedback ID {}: {}", feedback.getIdFeedback(), feedback.getEventTitle());
            }
        });
        return feedbacks;
    }

    @Override
    public Feedback addFeedbackAndAffectToEvents(Feedback feedback, Long eventId) {
        log.info("Adding feedback and linking to event ID: {}", eventId);
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isPresent()) {
            feedback.setEvents(event.get());
            feedback.setEventTitle(event.get().getTitle()); // Remplir eventTitle
            return feedbackRepository.save(feedback);
        } else {
            log.warn("Event with ID: {} not found", eventId);
            throw new IllegalArgumentException("Event with ID " + eventId + " not found");
        }
    }

    @Override
    public List<Feedback> getFeedbacksByEventTitle(String eventTitle) {
        log.info("Fetching feedbacks for eventTitle: {}", eventTitle);
        List<Feedback> feedbacks;
        if (eventTitle == null || eventTitle.isBlank()) {
            feedbacks = feedbackRepository.findAll();
        } else {
            feedbacks = feedbackRepository.findByEventTitle(eventTitle);
        }
        // Ensure eventTitle is populated for each feedback
        feedbacks.forEach(feedback -> {
            if (feedback.getEventTitle() == null && feedback.getEvents() != null) {
                feedback.setEventTitle(feedback.getEvents().getTitle());
                log.info("Populated eventTitle for feedback ID {}: {}", feedback.getIdFeedback(), feedback.getEventTitle());
            }
        });
        return feedbacks;
    }


   /* @Override
    public FeedbackStats getFeedbackStats(String eventTitle) {
        log.info("Calculating stats for eventTitle: {}", eventTitle);
        List<Feedback> feedbacks = getFeedbacksByEventTitle(eventTitle);

        // Calculer la moyenne des feelings par eventTitle
        Map<String, Double> averageFeelingByEvent = feedbacks.stream()
                .collect(Collectors.groupingBy(
                        Feedback::getEventTitle,
                        Collectors.averagingInt(feedback -> feedback.getFeeling().getValue())
                ));

        return new FeedbackStats(averageFeelingByEvent);
    }*/

    @Override
    public FeedbackStats getSentimentStatistics(String eventTitle) {
        log.info("Calculating sentiment statistics for eventTitle: {}", eventTitle);

        List<Feedback> feedbacks = eventTitle == null || eventTitle.isBlank()
                ? feedbackRepository.findAll()
                : feedbackRepository.findByEventTitle(eventTitle);

        long totalFeedbacks = feedbacks.size();
        long nullSentimentCount = feedbacks.stream().filter(f -> f.getSentiment() == null).count();
        log.info("Feedbacks with null sentiment: {}", nullSentimentCount);

        Map<String, Long> sentimentCounts = new HashMap<>();
        Map<String, Double> sentimentPercentages = new HashMap<>();

        // Initialiser les compteurs pour tous les sentiments valides
        sentimentAnalysisService.getValidSentiments().forEach(sentiment -> sentimentCounts.put(sentiment, 0L));

        // Compter les sentiments, reclasser les invalides et null comme NEUTRAL
        feedbacks.forEach(f -> {
            String sentiment = f.getSentiment();
            if (sentiment == null) {
                log.warn("Sentiment null trouvé dans feedback ID {}. Reclassé comme NEUTRAL.", f.getIdFeedback());
                sentimentCounts.merge("NEUTRAL", 1L, Long::sum);
            } else if (sentimentAnalysisService.getValidSentiments().contains(sentiment)) {
                sentimentCounts.merge(sentiment, 1L, Long::sum);
            } else {
                log.warn("Sentiment invalide trouvé dans feedback ID {}: {}. Reclassé comme NEUTRAL.", f.getIdFeedback(), sentiment);
                sentimentCounts.merge("NEUTRAL", 1L, Long::sum);
            }
        });

        // Calculer les pourcentages
        if (totalFeedbacks > 0) {
            sentimentCounts.forEach((sentiment, count) -> {
                double percentage = (count.doubleValue() / totalFeedbacks) * 100.0;
                sentimentPercentages.put(sentiment, Math.round(percentage * 100.0) / 100.0);
            });
        }

        FeedbackStats stats = new FeedbackStats();
        stats.setTotalFeedbacks(totalFeedbacks);
        stats.setEventTitle(eventTitle);
        stats.setSentimentCounts(sentimentCounts);
        stats.setSentimentPercentages(sentimentPercentages);

        log.info("Sentiment statistics calculated: {}", stats);
        return stats;
    }
}

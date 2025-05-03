package tn.esprit.examen.Smartmeet.Services.MaryemSalhi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.model.process.internal.UserTypeResolution;
import org.springframework.stereotype.Service;
import tn.esprit.examen.Smartmeet.dto.MaryemSalhi.FeedbackStats;
import tn.esprit.examen.Smartmeet.entities.MaryemSalhi.Feedback;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Event;
import tn.esprit.examen.Smartmeet.entities.Users.Users;
import tn.esprit.examen.Smartmeet.repositories.MaryemSalhi.IFeedbackRepository;
import tn.esprit.examen.Smartmeet.repositories.SalmaBenRomdhan.IEventRepository;
import tn.esprit.examen.Smartmeet.repositories.Users.UserRepository;

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
    private final UserRepository userRepository;
    private final IEventRepository eventRepository;
    private final GeminiService geminiService;
    private final SentimentAnalysisService sentimentAnalysisService;

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
            feedback.setMessage(professionalMessage);
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
        List<Feedback> feedbacks = feedbackRepository.findAllWithUser();
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

        // Récupérer l'utilisateur à partir du userID envoyé
        if (feedback.getUser() != null && feedback.getUser().getUserID() != null) {
            Optional<Users> userOpt = userRepository.findByUserID(feedback.getUser().getUserID());
            if (userOpt.isPresent()) {
                feedback.setUser(userOpt.get());
                log.info("Utilisateur associé au feedback: {}", feedback.getUser().getUserID());
            } else {
                log.warn("Utilisateur avec userID: {} non trouvé", feedback.getUser().getUserID());
                throw new IllegalArgumentException("Utilisateur avec userID " + feedback.getUser().getUserID() + " non trouvé");
            }
        } else {
            log.warn("Aucun utilisateur fourni pour le feedback");
            throw new IllegalArgumentException("Utilisateur requis pour ajouter un feedback");
        }

        // Associer l'événement
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isPresent()) {
            feedback.setEvents(event.get());
            feedback.setEventTitle(event.get().getTitle());

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
                feedback.setMessage(professionalMessage);
            }

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
            feedbacks = feedbackRepository.findAllWithUser();
        } else {
            feedbacks = feedbackRepository.findByEventTitleWithUser(eventTitle);
        }
        feedbacks.forEach(feedback -> {
            if (feedback.getEventTitle() == null && feedback.getEvents() != null) {
                feedback.setEventTitle(feedback.getEvents().getTitle());
                log.info("Populated eventTitle for feedback ID {}: {}", feedback.getIdFeedback(), feedback.getEventTitle());
            }
        });
        return feedbacks;
    }

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

        sentimentAnalysisService.getValidSentiments().forEach(sentiment -> sentimentCounts.put(sentiment, 0L));

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

    @Override
    public List<Feedback> getFeedbackByUserId(Long userId) {
        log.info("Fetching feedbacks for user ID: {}", userId);
        List<Feedback> feedbacks = feedbackRepository.findByUserUserID(userId);
        feedbacks.forEach(feedback -> {
            if (feedback.getEventTitle() == null && feedback.getEvents() != null) {
                feedback.setEventTitle(feedback.getEvents().getTitle());
                log.info("Populated eventTitle for feedback ID {}: {}", feedback.getIdFeedback(), feedback.getEventTitle());
            }
        });
        return feedbacks;
    }

    @Override
    public Feedback updateFeedbackWithEventAndUser(Feedback feedback, Long eventId, Long userId) {
        log.info("Updating feedback ID: {} for event ID: {} and user ID: {}", feedback.getIdFeedback(), eventId, userId);
        Optional<Feedback> existingFeedbackOpt = feedbackRepository.findById(feedback.getIdFeedback());
        if (!existingFeedbackOpt.isPresent()) {
            log.warn("Feedback with ID: {} not found", feedback.getIdFeedback());
            throw new IllegalArgumentException("Feedback with ID " + feedback.getIdFeedback() + " not found");
        }
        Feedback existingFeedback = existingFeedbackOpt.get();
        if (existingFeedback.getUser() == null || !existingFeedback.getUser().getUserID().equals(userId)) {
            log.warn("User ID: {} is not authorized to update feedback ID: {}", userId, feedback.getIdFeedback());
            throw new SecurityException("User is not authorized to update this feedback");
        }
        Optional<Event> event = eventRepository.findById(eventId);
        if (!event.isPresent()) {
            log.warn("Event with ID: {} not found", eventId);
            throw new IllegalArgumentException("Event with ID " + eventId + " not found");
        }
        Optional<Users> user = userRepository.findByUserID(userId);
        if (!user.isPresent()) {
            log.warn("User with ID: {} not found", userId);
            throw new IllegalArgumentException("User with ID " + userId + " not found");
        }
        feedback.setEvents(event.get());
        feedback.setEventTitle(event.get().getTitle());
        feedback.setUser(user.get());
        return feedbackRepository.save(feedback);
    }

}
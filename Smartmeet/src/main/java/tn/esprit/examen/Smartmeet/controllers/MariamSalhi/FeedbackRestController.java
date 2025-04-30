package tn.esprit.examen.Smartmeet.controllers.MariamSalhi;

import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.examen.Smartmeet.Services.MaryemSalhi.GeminiService;
import tn.esprit.examen.Smartmeet.Services.MaryemSalhi.IFeedbackServices;
import tn.esprit.examen.Smartmeet.Services.MaryemSalhi.MailingService;
import tn.esprit.examen.Smartmeet.Services.MaryemSalhi.SentimentAnalysisService;
import tn.esprit.examen.Smartmeet.dto.MaryemSalhi.FeedbackStats;
import tn.esprit.examen.Smartmeet.entities.MaryemSalhi.Feedback;

import java.util.List;


@RequestMapping("FeedBack")
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@Tag(name="hello")
@RequiredArgsConstructor
@Slf4j

public class FeedbackRestController {
    private final IFeedbackServices servicesFeedback;
    private final MailingService mailingService;
    private final GeminiService geminiService; // Ton service qui utilise l'API Gemini
    private final SentimentAnalysisService sentimentAnalysisService; // Service pour la détection de sentiment

    @PostMapping("/Add-feedbacks")
    public ResponseEntity<Feedback> addFeedback(@RequestBody Feedback feedback) {
        log.info("Adding feedback: {}", feedback);

        try {
            // Étape 1 : Reformuler le message avec Gemini
            String originalMessage = feedback.getMessage();
            if (originalMessage != null && !originalMessage.isBlank()) {
                String reformulatedMessage = geminiService.reformulateMessage(originalMessage);
                feedback.setMessage(reformulatedMessage);
                log.info("Message reformulated by Gemini: {}", reformulatedMessage);
            } else {
                log.warn("Message is empty or null, skipping reformulation.");
            }

            // Étape 2 : Détecter le sentiment
            try {
                String sentiment = sentimentAnalysisService.analyzeSentiment(originalMessage);
                feedback.setSentiment(sentiment);
                log.info("Sentiment detected: {}", sentiment);
            } catch (Exception e) {
                log.error("Failed to analyze sentiment: {}", e.getMessage());
                feedback.setSentiment("NEUTRAL");
            }

            // Étape 3 : Sauvegarder le feedback
            Feedback savedFeedback = servicesFeedback.addFeedback(feedback);

            // Étape 4 : Envoyer une notification par email
            String subject = "New Feedback Submitted";
            String message = "A new feedback has been submitted:\n\n" +
                    "Event Title: " + savedFeedback.getEventTitle() + "\n" +
                    "Message: " + savedFeedback.getMessage() + "\n" +
                    "Sentiment: " + savedFeedback.getSentiment() + "\n";
            String recipientEmail = "mariam.salhiai@gmail.com";
            mailingService.sendVerificationCode(recipientEmail, message);

            return ResponseEntity.ok(savedFeedback);

        } catch (Exception e) {
            log.error("Error while processing feedback: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }



    @PutMapping("/Update-feedbacks")
    public Feedback updateFeedback(@RequestBody Feedback feedback) {
        log.info("Updating feedback: {}", feedback);
        return servicesFeedback.updateFeedback(feedback);
    }

    @PutMapping("/Update-feedbacks/{id}/event/{eventId}")
    public Feedback updateFeedbackWithEvent(
            @PathVariable Long id,
            @PathVariable Long eventId,
            @RequestBody Feedback feedback) {
        log.info("Received update request - ID: {}, Event ID: {}, Feedback: {}", id, eventId, feedback);
        feedback.setIdFeedback(id); // Assure que l'ID du feedback est correct
        try {
            return servicesFeedback.updateFeedbackWithEvent(feedback, eventId);
        } catch (IllegalArgumentException e) {
            log.error("Error updating feedback: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("Delete-feedbacks/{id}")
    public void deleteFeedback(@PathVariable int id) {
        log.info("Deleting feedback with ID: {}", id);
        servicesFeedback.deleteFeedback(id);
    }

    @GetMapping("Get-feedbacks/{id}")
    public Feedback getFeedbackById(@PathVariable int id) {
        log.info("Fetching feedback with ID: {}", id);
        return servicesFeedback.getFeedbackById(id);
    }

    @GetMapping("Get-all-feedbacks")
    public List<Feedback> getAllFeedbacks() {
        log.info("Fetching all feedbacks");
        return servicesFeedback.getAllFeedbacks();
    }

    @PostMapping("/add-feedback-and-affect-to-event/{eventId}")
    public ResponseEntity<Feedback> addFeedbackAndAffectToEvents(@RequestBody Feedback feedback, @PathVariable Long eventId) {
        log.info("Adding feedback and linking to event ID: {}", eventId);

        try {
            // Étape 1 : Reformuler le message avec Gemini
            String originalMessage = feedback.getMessage();
            if (originalMessage != null && !originalMessage.isBlank()) {
                String reformulatedMessage = geminiService.reformulateMessage(originalMessage);
                feedback.setMessage(reformulatedMessage);
                log.info("Message reformulated by Gemini: {}", reformulatedMessage);
            } else {
                log.warn("Message is empty or null, skipping reformulation.");
            }

            // Étape 2 : Détecter le sentiment
            try {
                String sentiment = sentimentAnalysisService.analyzeSentiment(originalMessage);
                feedback.setSentiment(sentiment);
                log.info("Sentiment detected: {}", sentiment);
            } catch (Exception e) {
                log.error("Failed to analyze sentiment: {}", e.getMessage());
                feedback.setSentiment("NEUTRAL");
            }

            // Étape 3 : Sauvegarder et lier à l'événement
            Feedback savedFeedback = servicesFeedback.addFeedbackAndAffectToEvents(feedback, eventId);

            // Étape 4 : Envoyer une notification par email
            String subject = "New Feedback Submitted and Linked to Event";
            String message = "A new feedback has been submitted and linked to an event:\n\n" +
                    "Event Title: " + savedFeedback.getEventTitle() + "\n" +
                    "Message: " + savedFeedback.getMessage() + "\n" +
                    "Sentiment: " + savedFeedback.getSentiment() + "\n";
            String recipientEmail = "mariam.salhiai@gmail.com";
            mailingService.sendVerificationCode(recipientEmail, message);

            return ResponseEntity.ok(savedFeedback);

        } catch (Exception e) {
            log.error("Error while processing feedback and linking to event: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }


    @GetMapping("/filtered")
    public List<Feedback> getFeedbacksByEventTitle(@RequestParam(required = false) String eventTitle) {
        log.info("Fetching feedbacks for eventTitle: {}", eventTitle);
        return servicesFeedback.getFeedbacksByEventTitle(eventTitle);
    }

    @GetMapping("/sentiment-statistics")
    public FeedbackStats getSentimentStatistics(@RequestParam(required = false) String eventTitle) {
        log.info("Fetching sentiment statistics for eventTitle: {}", eventTitle);
        return servicesFeedback.getSentimentStatistics(eventTitle);
    }

    /*@GetMapping("/stats")
    public FeedbackStats getFeedbackStats(@RequestParam(required = false) String eventTitle) {
        log.info("Fetching stats for eventTitle: {}", eventTitle);
        return servicesFeedback.getFeedbackStats(eventTitle);
    }*/

}
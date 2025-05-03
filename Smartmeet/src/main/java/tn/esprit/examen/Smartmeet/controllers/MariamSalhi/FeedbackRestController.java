package tn.esprit.examen.Smartmeet.controllers.MariamSalhi;

import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.examen.Smartmeet.Services.MaryemSalhi.GeminiService;
import tn.esprit.examen.Smartmeet.Services.MaryemSalhi.IFeedbackServices;
import tn.esprit.examen.Smartmeet.Services.MaryemSalhi.MailingService;
import tn.esprit.examen.Smartmeet.Services.MaryemSalhi.SentimentAnalysisService;
import tn.esprit.examen.Smartmeet.dto.MaryemSalhi.FeedbackStats;
import tn.esprit.examen.Smartmeet.entities.MaryemSalhi.Feedback;
import tn.esprit.examen.Smartmeet.entities.Users.Users;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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
    @PutMapping("/Update-feedbacks/{idFeedback}/{eventId}/{userId}")
    @PreAuthorize("hasRole('USER') and #userId == authentication.principal.userID")
    public ResponseEntity<Feedback> updateFeedbackWithEventAndUser(
            @PathVariable Long idFeedback,
            @PathVariable Long eventId,
            @PathVariable Long userId,
            @RequestBody Feedback feedback) {
        log.info("Updating feedback ID: {} for event ID: {} and user ID: {}", idFeedback, eventId, userId);
        try {
            feedback.setIdFeedback(idFeedback);
            Feedback updatedFeedback = servicesFeedback.updateFeedbackWithEventAndUser(feedback, eventId, userId);
            String originalMessage = updatedFeedback.getMessage();
            if (originalMessage != null && !originalMessage.isBlank()) {
                String reformulatedMessage = geminiService.reformulateMessage(originalMessage);
                updatedFeedback.setMessage(reformulatedMessage);
                log.info("Message reformulated by Gemini: {}", reformulatedMessage);
            }
            try {
                String sentiment = sentimentAnalysisService.analyzeSentiment(originalMessage);
                updatedFeedback.setSentiment(sentiment);
                log.info("Sentiment detected: {}", sentiment);
            } catch (Exception e) {
                log.error("Failed to analyze sentiment: {}", e.getMessage());
                updatedFeedback.setSentiment("NEUTRAL");
            }
            updatedFeedback = servicesFeedback.updateFeedback(updatedFeedback);
            String subject = "Feedback Updated";
            String message = "A feedback has been updated:\n\n" +
                    "Feedback ID: " + updatedFeedback.getIdFeedback() + "\n" +
                    "Event Title: " + updatedFeedback.getEventTitle() + "\n" +
                    "Message: " + updatedFeedback.getMessage() + "\n" +
                    "Sentiment: " + updatedFeedback.getSentiment() + "\n" +
                    "User ID: " + userId + "\n";
            String recipientEmail = "mariam.salhiai@gmail.com";
            mailingService.sendVerificationCode(recipientEmail, message);
            return ResponseEntity.ok(updatedFeedback);
        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (SecurityException e) {
            log.error("Permission denied: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
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
    public ResponseEntity<List<Map<String, Object>>> getAllFeedbacks() {
        log.info("Fetching all feedbacks");
        List<Feedback> feedbacks = servicesFeedback.getAllFeedbacks();

        // Transform the response to include user data
        List<Map<String, Object>> response = feedbacks.stream().map(f -> {
            Map<String, Object> feedbackMap = new HashMap<>();
            feedbackMap.put("idFeedback", f.getIdFeedback());
            feedbackMap.put("message", f.getMessage());
            feedbackMap.put("date", f.getDate() != null ? f.getDate().toString() : null);
            feedbackMap.put("sentiment", f.getSentiment());
            feedbackMap.put("feeling", f.getFeeling());
            feedbackMap.put("eventTitle", f.getEventTitle());
            if (f.getUser() != null) {
                feedbackMap.put("user", Map.of(
                        "userID", f.getUser().getUserID(),
                        "username", f.getUser().getUsername() != null ? f.getUser().getUsername() : ""
                ));
            }
            return feedbackMap;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/add-feedback-and-affect-to-event/{eventId}/{userId}")
    public ResponseEntity<?> addFeedbackAndAffectToEvents(
            @RequestBody Feedback feedback,
            @PathVariable Long eventId,
            @PathVariable Long userId) {
        log.info("Adding feedback and linking to event ID: {} and user ID: {}", eventId, userId);

        try {
            // Étape 1 : Ajouter un utilisateur fictif à feedback pour la validation dans le service
            Users user = new Users();
            user.setUserID(userId);
            feedback.setUser(user);

            // Étape 2 : Sauvegarder et lier à l'événement et à l'utilisateur
            Feedback savedFeedback = servicesFeedback.addFeedbackAndAffectToEvents(feedback, eventId);

            // Étape 3 : Reformuler le message avec Gemini
            String originalMessage = savedFeedback.getMessage();
            if (originalMessage != null && !originalMessage.isBlank()) {
                String reformulatedMessage = geminiService.reformulateMessage(originalMessage);
                savedFeedback.setMessage(reformulatedMessage);
                log.info("Message reformulated by Gemini: {}", reformulatedMessage);
            } else {
                log.warn("Message is empty or null, skipping reformulation.");
            }

            // Étape 4 : Détecter le sentiment
            try {
                String sentiment = sentimentAnalysisService.analyzeSentiment(originalMessage);
                savedFeedback.setSentiment(sentiment);
                log.info("Sentiment detected: {}", sentiment);
            } catch (Exception e) {
                log.error("Failed to analyze sentiment: {}", e.getMessage());
                savedFeedback.setSentiment("NEUTRAL");
            }

            // Étape 5 : Sauvegarder les modifications (sentiment et message reformulé)
            savedFeedback = servicesFeedback.updateFeedback(savedFeedback);

            // Étape 6 : Envoyer une notification par email
            String subject = "New Feedback Submitted and Linked to Event";
            String message = "A new feedback has been submitted and linked to an event:\n\n" +
                    "Event Title: " + savedFeedback.getEventTitle() + "\n" +
                    "Message: " + savedFeedback.getMessage() + "\n" +
                    "Sentiment: " + savedFeedback.getSentiment() + "\n" +
                    "User ID: " + userId + "\n";
            String recipientEmail = "mariam.salhiai@gmail.com";
            mailingService.sendVerificationCode(recipientEmail, message);

            return ResponseEntity.ok(savedFeedback);

        } catch (IllegalArgumentException e) {
            log.error("Validation error while processing feedback: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while processing feedback and linking to event: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());
        }
    }

    @GetMapping("/filtered")
    public ResponseEntity<List<Map<String, Object>>> getFeedbacksByEventTitle(@RequestParam(required = false) String eventTitle) {
        log.info("Fetching feedbacks for eventTitle: {}", eventTitle);
        List<Feedback> feedbacks = servicesFeedback.getFeedbacksByEventTitle(eventTitle);
        List<Map<String, Object>> response = feedbacks.stream().map(f -> {
            Map<String, Object> feedbackMap = new HashMap<>();
            feedbackMap.put("idFeedback", f.getIdFeedback());
            feedbackMap.put("message", f.getMessage());
            feedbackMap.put("date", f.getDate() != null ? f.getDate().toString() : null);
            feedbackMap.put("sentiment", f.getSentiment());
            feedbackMap.put("feeling", f.getFeeling());
            feedbackMap.put("eventTitle", f.getEventTitle());
            if (f.getUser() != null) {
                feedbackMap.put("user", Map.of(
                        "userID", f.getUser().getUserID(),
                        "username", f.getUser().getUsername() != null ? f.getUser().getUsername() : ""
                ));
            }
            return feedbackMap;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(response);
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
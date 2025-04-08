package tn.esprit.examen.Smartmeet.controllers;

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

    @PostMapping("/Add-feedbacks")
    public ResponseEntity<Feedback> addFeedback(@RequestBody Feedback feedback) {
        log.info("Adding feedback: {}", feedback);

        try {
            // Appel à Gemini pour reformuler le message
            String originalMessage = feedback.getMessage();
            String reformulatedMessage = geminiService.reformulateMessage(originalMessage); // ⬅ reformulation ici
            feedback.setMessage(reformulatedMessage); // ⬅ On remplace l'ancien message par le reformulé
            log.info("Message reformulated by Gemini: {}", reformulatedMessage);
        } catch (Exception e) {
            log.error("Failed to reformulate message with Gemini: {}", e.getMessage());
            // Tu peux choisir de continuer avec le message original ou retourner une erreur
        }

        Feedback savedFeedback = servicesFeedback.addFeedback(feedback);

        // Send email notification after feedback is saved
        String subject = "New Feedback Submitted";
        String message = "A new feedback has been submitted:\n\n" +
                "Event Title: " + savedFeedback.getEventTitle() + "\n" +
                "Message: " + savedFeedback.getMessage() + "\n";
        String recipientEmail = "mariam.salhiai@gmail.com";
        mailingService.sendVerificationCode(recipientEmail, message);

        return ResponseEntity.ok(savedFeedback);
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
    public Feedback addFeedbackAndAffectToEvents(@RequestBody Feedback feedback, @PathVariable Long eventId) {
        log.info("Adding feedback and linking to event ID: {}", eventId);
        return servicesFeedback.addFeedbackAndAffectToEvents(feedback, eventId);
    }

}
package tn.esprit.examen.Smartmeet.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.examen.Smartmeet.Services.MaryemSalhi.IFeedbackServices;
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

    @PostMapping("/Add-feedbacks")
    public Feedback addFeedback(@RequestBody Feedback feedback) {
        log.info("Adding feedback: {}", feedback);
        return servicesFeedback.addFeedback(feedback);
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
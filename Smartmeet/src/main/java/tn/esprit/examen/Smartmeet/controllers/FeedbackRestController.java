package tn.esprit.examen.Smartmeet.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.Smartmeet.Services.MaryemSalhi.IFeedbackServices;
import tn.esprit.examen.Smartmeet.entities.MaryemSalhi.Feedback;

import java.util.List;


@RequestMapping("FeedBack")
@RestController
@Tag(name="hello")

public class FeedbackRestController {
    private  IFeedbackServices servicesFeedback;
    @PostMapping("/Add-feedbacks")
    public Feedback addFeedback(@RequestBody Feedback feedback) {
        return servicesFeedback.addFeedback(feedback);
    }

    @PutMapping("/Update-feedbacks")
    public Feedback updateFeedback(@RequestBody Feedback feedback) {
        return servicesFeedback.updateFeedback(feedback);
    }

    @DeleteMapping("Delete-feedbacks/{id}")
    public void deleteFeedback(@PathVariable int id) {
        servicesFeedback.deleteFeedback(id);
    }

    @GetMapping("Get-feedbacks/{id}")
    public Feedback getFeedbackById(@PathVariable int id) {
        return servicesFeedback.getFeedbackById(id);
    }

    @GetMapping("Get-all-feedbacks")
    public List<Feedback> getAllFeedbacks() {
        return servicesFeedback.getAllFeedbacks();
    }

    @PostMapping("/add-feedback-and-affect-to-event/{eventId}")
    public Feedback addFeedbackAndAffectToEvents(@RequestBody Feedback feedback, @PathVariable Long eventId) {
        return servicesFeedback.addFeedbackAndAffectToEvents(feedback, eventId);
    }


}

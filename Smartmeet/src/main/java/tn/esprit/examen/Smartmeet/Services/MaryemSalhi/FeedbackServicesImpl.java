package tn.esprit.examen.Smartmeet.Services.MaryemSalhi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.examen.Smartmeet.entities.MaryemSalhi.Feedback;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Event;
import tn.esprit.examen.Smartmeet.repositories.MaryemSalhi.IFeedbackRepository;
import tn.esprit.examen.Smartmeet.repositories.SalmaBenRomdhan.IEventRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service

public class FeedbackServicesImpl implements IFeedbackServices {

    private final IFeedbackRepository feedbackRepository;
    private final IEventRepository eventRepository;
    private final GeminiService geminiService;
    @Override
    public Feedback addFeedback(Feedback feedback) {
        log.info("Adding feedback: {}", feedback);

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
        Optional<Feedback> feedback = feedbackRepository.findById(Long.valueOf(id));
        return feedback.orElse(null);
    }

    @Override
    public List<Feedback> getAllFeedbacks() {
        log.info("Fetching all feedbacks");
        return feedbackRepository.findAll();
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


}

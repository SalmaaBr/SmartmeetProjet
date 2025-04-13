package tn.esprit.examen.Smartmeet.Services.MaryemSalhi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.examen.Smartmeet.entities.MaryemSalhi.Feedback;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Event;
import tn.esprit.examen.Smartmeet.repositories.MaryemSalhi.IFeedbackRepository;
import tn.esprit.examen.Smartmeet.repositories.SalmaBenRomdhan.IEventRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service

public class FeedbackServicesImpl implements IFeedbackServices {

    private final IFeedbackRepository feedbackRepository;
    private final IEventRepository eventRepository;

    @Override
    public Feedback addFeedback(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    @Override
    public Feedback updateFeedback(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }



    @Override
    public void deleteFeedback(Integer id) {
        feedbackRepository.deleteById(id);


    }

    @Override
    public Feedback getFeedbackById(Integer id) {
        return feedbackRepository.findById(id).orElse(null);
    }

    @Override
    public List<Feedback> getAllFeedbacks() {
        return feedbackRepository.findAll();
    }

    @Override
    public Feedback addFeedbackAndAffectToEvents(Feedback feedback, Long eventId) {
        Event event = eventRepository.findById(eventId).orElse(null);
        if (event != null) {
            feedback.setEvent(event);
            return feedbackRepository.save(feedback);
        }
        return null;
    }



}

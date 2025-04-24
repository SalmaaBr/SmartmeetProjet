package tn.esprit.examen.Smartmeet.Services.SalmaBenRomdhan;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Event;
import tn.esprit.examen.Smartmeet.entities.Users.Users;
import tn.esprit.examen.Smartmeet.repositories.SalmaBenRomdhan.IEventRepository;
import tn.esprit.examen.Smartmeet.repositories.Users.UserRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class EventServicesImpl implements IEventServices {


    private final IEventRepository IEventRepository;
    private final UserRepository userRepository;


    public Event createEvent(Event event) {
        return IEventRepository.save(event);
    }

    @Override
    public Event updateEvent(Long id, Event event) {
        Event existingEvent = IEventRepository.findById(id).orElseThrow(() -> new RuntimeException("Event not found"));
        existingEvent.setTypeevent(event.getTypeevent());
        existingEvent.setTypetheme(event.getTypetheme());
        existingEvent.setTitle(event.getTitle());
        existingEvent.setDescription(event.getDescription());
        existingEvent.setLocation(event.getLocation());
        existingEvent.setTypeweather(event.getTypeweather());
        existingEvent.setStartTime(event.getStartTime());
        existingEvent.setEndTime(event.getEndTime());
        existingEvent.setMaxParticipants(event.getMaxParticipants());
        return IEventRepository.save(existingEvent);
    }

    @Override
    public void deleteEvent(Long id) {
        IEventRepository.deleteById(id);
    }

    @Override
    public Event getEventById(Long id) {
        return IEventRepository.findById(id).orElseThrow(() -> new RuntimeException("Event not found"));
    }

    @Override
    public List<Event> getAllEvents() {
        return IEventRepository.findAll();
    }

    @Override
    public void addAndAssignEventToUser(Long userId, Long eventId) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        Event event = IEventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Événement non trouvé"));

        user.getEvents().add(event);
        userRepository.save(user);
    }


}

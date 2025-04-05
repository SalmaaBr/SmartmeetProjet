package tn.esprit.examen.Smartmeet.Services.SalmaBenRomdhan;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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


    @Override
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


    @Transactional
    @Override
    public int addAndAssignEventToUser(Long eventId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

        Event event = IEventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));

        if (event.getMaxParticipants() <= 0) {
            throw new RuntimeException("L'événement est complet");
        }

        if (event.getUsers().contains(user)) {
            throw new RuntimeException("Vous êtes déjà inscrit à cet événement");
        }

        user.getEvents().add(event);
        event.getUsers().add(user);

        event.setMaxParticipants(event.getMaxParticipants() - 1);

        userRepository.save(user);
        IEventRepository.save(event);

        return event.getMaxParticipants(); // <-- retourner ici le nouveau nombre
    }



}

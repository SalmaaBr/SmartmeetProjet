package tn.esprit.examen.Smartmeet.Services.SalmaBenRomdhan;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.examen.Smartmeet.email.EmailService;
import tn.esprit.examen.Smartmeet.email.EmailTemplateName;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Event;
import tn.esprit.examen.Smartmeet.entities.Users.Users;
import tn.esprit.examen.Smartmeet.repositories.SalmaBenRomdhan.IEventRepository;
import tn.esprit.examen.Smartmeet.repositories.Users.UserRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class EventServicesImpl implements IEventServices {

    @Autowired
    private EmailService emailService;
    private final IEventRepository IEventRepository;
    private final UserRepository userRepository;


    @Override
    public Event createEvent(Event event) {
        return IEventRepository.save(event);
    }

    public Event updateEvent(Long id, Event updatedEvent) {
        Event existingEvent = IEventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // Mise à jour des champs de l’événement
        existingEvent.setTitle(updatedEvent.getTitle());
        existingEvent.setDescription(updatedEvent.getDescription());
        existingEvent.setLocation(updatedEvent.getLocation());
        existingEvent.setTypeevent(updatedEvent.getTypeevent());
        existingEvent.setTypetheme(updatedEvent.getTypetheme());
        existingEvent.setTypeweather(updatedEvent.getTypeweather());
        existingEvent.setStartTime(updatedEvent.getStartTime());
        existingEvent.setEndTime(updatedEvent.getEndTime());
        existingEvent.setMaxParticipants(updatedEvent.getMaxParticipants());
        existingEvent.setLatitude(updatedEvent.getLatitude());
        existingEvent.setLongitude(updatedEvent.getLongitude());

        // Si un nouveau fichier a été uploadé
        if (updatedEvent.getFilePath() != null && !updatedEvent.getFilePath().isEmpty()) {
            existingEvent.setFilePath(updatedEvent.getFilePath());
        }

        Event savedEvent = IEventRepository.save(existingEvent);

        // ✅ Envoi d’un email à chaque participant
        if (savedEvent.getUsers() != null) {
            for (Users participant : savedEvent.getUsers()) {
                if (participant.getEmail() != null) {
                    emailService.sendEventUpdateEmail(
                            participant.getEmail(),
                            participant.getUsername(),
                            savedEvent.getTitle()
                    );
                }
            }
        }

        return savedEvent;
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

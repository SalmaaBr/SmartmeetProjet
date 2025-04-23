package tn.esprit.examen.Smartmeet.Services.SalmaBenRomdhan;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Event;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.EventLike;
import tn.esprit.examen.Smartmeet.entities.Users.Users;
import tn.esprit.examen.Smartmeet.repositories.SalmaBenRomdhan.EventLikeRepository;
import tn.esprit.examen.Smartmeet.repositories.SalmaBenRomdhan.IEventRepository;
import tn.esprit.examen.Smartmeet.repositories.Users.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventLikeService {

    private final EventLikeRepository eventLikeRepository;
    private final IEventRepository eventRepository;
    private final UserRepository userRepository;

    @Transactional
    public String toggleLike(Long eventId) {
        // Get the authenticated user's username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Find the user by username
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Find the event by eventId
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        // Check if the user has already liked the event
        Optional<EventLike> existingLike = eventLikeRepository.findByUserAndEvent(user, event);

        if (existingLike.isPresent()) {
            EventLike eventLike = existingLike.get();
            if (eventLike.getLikes() == 1) {
                // User already liked, so unlike (set to 0)
                eventLike.setLikes(0);
                eventLikeRepository.save(eventLike);
                // Decrement event likes count
                event.setLikes(event.getLikes() - 1);
                eventRepository.save(event);
                return "Event unliked successfully";
            } else {
                // User has unliked, so like (set to 1)
                eventLike.setLikes(1);
                eventLikeRepository.save(eventLike);
                // Increment event likes count
                event.setLikes(event.getLikes() + 1);
                eventRepository.save(event);
                return "Event liked successfully";
            }
        } else {
            // No like exists, create a new like
            EventLike eventLike = new EventLike();
            eventLike.setUser(user);
            eventLike.setEvent(event);
            eventLike.setLikes(1);
            eventLikeRepository.save(eventLike);
            // Increment event likes count
            event.setLikes(event.getLikes() + 1);
            eventRepository.save(event);
            return "Event liked successfully";
        }
    }

    public int getLikeStatus(Long eventId) {
        // Récupérer l'utilisateur authentifié
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Le nom d'utilisateur (souvent l'email)

        // Récupérer l'entité User à partir du username
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Récupérer l'événement
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        // Renvoyer le statut du like
        return eventLikeRepository.findByUserAndEvent(user, event)
                .map(EventLike::getLikes)
                .orElse(0);
    }


    public long getTotalLikes(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
        return eventLikeRepository.countByEventAndLikes(event, 1);
    }

    public List<EventLike> getAllEventLikes() {
        return eventLikeRepository.findAll();
    }
}

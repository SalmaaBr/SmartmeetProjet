package tn.esprit.examen.Smartmeet.Services.SalmaBenRomdhan;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.EventUserCalendar;
import tn.esprit.examen.Smartmeet.entities.Users.Users;
import tn.esprit.examen.Smartmeet.repositories.SalmaBenRomdhan.IEventUserCalendarRepository;
import tn.esprit.examen.Smartmeet.repositories.Users.UserRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class EventUserCalendarImpl implements IEventUserCalendarServices {

    @Autowired
    private IEventUserCalendarRepository eventUserCalendarRepository;

    @Autowired
    private UserRepository userRepository;

    public EventUserCalendar addEvent(EventUserCalendar event, String username) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé : " + username));
        event.setUsers(user);
        if (event.getCreatedDate() != null) {
            event.setStartDate(event.getCreatedDate());
        }
        return eventUserCalendarRepository.save(event);
    }

    @Override
    public List<EventUserCalendar> getUserEvents(String username) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé : " + username));
        return eventUserCalendarRepository.findByUsers(user);
    }
}

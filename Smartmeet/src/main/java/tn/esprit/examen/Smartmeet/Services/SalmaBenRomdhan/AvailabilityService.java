package tn.esprit.examen.Smartmeet.Services.SalmaBenRomdhan;

import org.springframework.stereotype.Service;
import tn.esprit.examen.Smartmeet.repositories.SalmaBenRomdhan.IEventRepository;
import tn.esprit.examen.Smartmeet.repositories.SalmaBenRomdhan.IEventUserCalendarRepository;

import java.time.LocalDateTime;

@Service
public class AvailabilityService {
    private final IEventRepository eventRepository;
    private final IEventUserCalendarRepository calendarRepository;

    public AvailabilityService(IEventRepository eventRepository, IEventUserCalendarRepository calendarRepository) {
        this.eventRepository = eventRepository;
        this.calendarRepository = calendarRepository;
    }

    public boolean isUserAvailable(Long userId, LocalDateTime start, LocalDateTime end) {
        // Vérifier les événements
        boolean hasEventConflict = eventRepository.existsUserEventsBetween(userId, start, end);

        // Vérifier le calendrier
        boolean hasCalendarConflict = calendarRepository.existsUserCalendarEventsBetween(userId, start, end);

        return !hasEventConflict && !hasCalendarConflict;
    }

    public LocalDateTime findNextAvailableSlot(Long userId, Integer durationMinutes) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime checkTime = now.withMinute(0).withSecond(0).withNano(0);

        // On cherche dans les 7 prochains jours
        for (int i = 0; i < 7 * 24; i++) {
            LocalDateTime slotStart = checkTime.plusHours(i);
            LocalDateTime slotEnd = slotStart.plusMinutes(durationMinutes);

            if (isUserAvailable(userId, slotStart, slotEnd)) {
                return slotStart;
            }
        }

        throw new RuntimeException("No available slot found in the next 7 days");
    }
}

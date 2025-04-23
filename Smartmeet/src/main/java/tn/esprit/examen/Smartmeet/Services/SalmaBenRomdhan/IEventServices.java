package tn.esprit.examen.Smartmeet.Services.SalmaBenRomdhan;

import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Event;

import java.util.List;
import java.util.Map;

public interface IEventServices {
    Event createEvent(Event event);
    Event updateEvent(Long id, Event event);
    void deleteEvent(Long id);
    Event getEventById(Long id);
    List<Event> getAllEvents();
    int addAndAssignEventToUser(Long eventId);
}

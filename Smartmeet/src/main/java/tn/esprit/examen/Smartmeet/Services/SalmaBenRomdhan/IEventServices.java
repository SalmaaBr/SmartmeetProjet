package tn.esprit.examen.Smartmeet.Services.SalmaBenRomdhan;

import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Event;

import java.util.List;

public interface IEventServices {
    Event createEvent(Event event, String imagePath);
    Event updateEvent(Long id, Event event);
    void deleteEvent(Long id);
    Event getEventById(Long id);
    List<Event> getAllEvents();
    void addAndAssignEventToUser(Long userId, Long eventId);

}

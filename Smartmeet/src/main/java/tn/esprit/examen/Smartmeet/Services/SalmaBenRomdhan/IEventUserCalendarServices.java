package tn.esprit.examen.Smartmeet.Services.SalmaBenRomdhan;

import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.EventUserCalendar;

import java.util.List;

public interface IEventUserCalendarServices {
    EventUserCalendar addEvent(EventUserCalendar event, String username);
    List<EventUserCalendar> getUserEvents(String username);
}

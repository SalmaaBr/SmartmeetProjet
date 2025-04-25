package tn.esprit.examen.Smartmeet.controllers.SalmaBenRomdhan;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.Smartmeet.Services.SalmaBenRomdhan.IEventUserCalendarServices;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.EventUserCalendar;

import java.util.List;

@CrossOrigin("*")
@RequiredArgsConstructor
@RequestMapping("/usercalendar")
@RestController
public class EventUserCalendarController {

    @Autowired
    private IEventUserCalendarServices eventUserCalendarService;

    @PostMapping("/addeventcalendar")
    public EventUserCalendar addEvent(@RequestBody EventUserCalendar event, Authentication authentication) {
        return eventUserCalendarService.addEvent(event, authentication.getName());
    }


    @GetMapping("/events")
    public List<EventUserCalendar> getUserEvents(Authentication authentication) {
        return eventUserCalendarService.getUserEvents(authentication.getName());
    }
}
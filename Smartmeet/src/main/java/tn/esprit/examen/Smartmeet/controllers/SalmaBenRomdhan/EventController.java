package tn.esprit.examen.Smartmeet.controllers.SalmaBenRomdhan;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Event;
import tn.esprit.examen.Smartmeet.Services.SalmaBenRomdhan.IEventServices;

import java.util.List;
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RequiredArgsConstructor
@RequestMapping("/event")
@RestController
public class EventController {

    private final IEventServices eventService;

    @PostMapping("/createevent")
    public Event createEvent(@RequestBody Event event) {
        System.out.println("Données reçues : " + event);
        return eventService.createEvent(event);
    }

    @PutMapping("updateevent/{id}")
    public Event updateEvent(@PathVariable Long id, @RequestBody Event event) {
        return eventService.updateEvent(id, event);
    }

    @DeleteMapping("deleteevent/{id}")
    public void deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
    }

    @GetMapping("getevent/{id}")
    public Event getEventById(@PathVariable Long id) {
        return eventService.getEventById(id);
    }

    @GetMapping("/getallevent")
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }


    @PostMapping("/assign/{userId}/{eventId}")
    public ResponseEntity<String> assignEventToUser(@PathVariable Long userId, @PathVariable Long eventId) {
        eventService.addAndAssignEventToUser(userId, eventId);
        return ResponseEntity.ok("Événement assigné avec succès !");
    }

}

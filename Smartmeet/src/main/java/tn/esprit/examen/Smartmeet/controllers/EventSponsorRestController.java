package tn.esprit.examen.Smartmeet.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.Smartmeet.Services.GhanemRidene.IEventSponsorServices;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.EventSponsor;


import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/EventSponsors")

public class EventSponsorRestController {

    private final IEventSponsorServices eventSponsorServices;

    @PostMapping
    public ResponseEntity<EventSponsor> createEventSponsor(@RequestBody EventSponsor eventSponsor) {
        EventSponsor createdEventSponsor = eventSponsorServices.createEventSponsor(eventSponsor);
        return new ResponseEntity<>(createdEventSponsor, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventSponsor> updateEventSponsor(@PathVariable Long id, @RequestBody EventSponsor eventSponsor) {
        EventSponsor updatedEventSponsor = eventSponsorServices.updateEventSponsor(id, eventSponsor);
        return new ResponseEntity<>(updatedEventSponsor, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEventSponsor(@PathVariable Long id) {
        eventSponsorServices.deleteEventSponsor(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventSponsor> getEventSponsorById(@PathVariable Long id) {
        EventSponsor eventSponsor = eventSponsorServices.getEventSponsorById(id);
        return eventSponsor != null ? new ResponseEntity<>(eventSponsor, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping
    public ResponseEntity<List<EventSponsor>> getAllEventSponsors() {
        List<EventSponsor> eventSponsors = eventSponsorServices.getAllEventSponsors();
        return new ResponseEntity<>(eventSponsors, HttpStatus.OK);
    }

  @PostMapping("/assign/{sponsorId}/{eventId}")
  public ResponseEntity<EventSponsor> assignSponsorToEvent(
    @PathVariable Long sponsorId,
    @PathVariable Long eventId,
    @RequestParam String avantages) {

    EventSponsor eventSponsor = eventSponsorServices.assignSponsorToEvent(sponsorId, eventId, avantages);
    if (eventSponsor != null) {
      return ResponseEntity.ok(eventSponsor);
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }

}

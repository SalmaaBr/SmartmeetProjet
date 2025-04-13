package tn.esprit.examen.Smartmeet.Services.GhanemRidene;


import tn.esprit.examen.Smartmeet.entities.GhanemRidene.EventSponsor;

import java.util.List;

public interface IEventSponsorServices {
    EventSponsor createEventSponsor(EventSponsor eventSponsor);
    EventSponsor updateEventSponsor(Long id, EventSponsor eventSponsor);
    void deleteEventSponsor(Long id);
    EventSponsor getEventSponsorById(Long id);
    List<EventSponsor> getAllEventSponsors();
    EventSponsor assignSponsorToEvent(Long sponsorId, Long eventId, String avantages);
}

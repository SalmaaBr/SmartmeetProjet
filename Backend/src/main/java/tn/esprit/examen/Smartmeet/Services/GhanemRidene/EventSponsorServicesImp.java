package tn.esprit.examen.Smartmeet.Services.GhanemRidene;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.EventSponsor;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.Sponsor;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Event;
import tn.esprit.examen.Smartmeet.repositories.GhanemRiden.IEventSponsorRepository;
import tn.esprit.examen.Smartmeet.repositories.GhanemRiden.ISponsorRepository;
import tn.esprit.examen.Smartmeet.repositories.SalmaBenRomdhan.IEventRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service

public class EventSponsorServicesImp implements IEventSponsorServices {


    private final IEventSponsorRepository ieventSponsorRepository;
    private final IEventRepository eventRepository;
    private final ISponsorRepository sponsorRepository;

    @Override
    public EventSponsor createEventSponsor(EventSponsor eventSponsor) {
        return ieventSponsorRepository.save(eventSponsor);
    }

    @Override
    public EventSponsor updateEventSponsor(Long id, EventSponsor eventSponsor) {
        eventSponsor.setId(id);
        return ieventSponsorRepository.save(eventSponsor);
    }

    @Override
    public void deleteEventSponsor(Long id) {
        ieventSponsorRepository.deleteById(id);
    }

    @Override
    public EventSponsor getEventSponsorById(Long id) {
        Optional<EventSponsor> optionalEventSponsor = ieventSponsorRepository.findById(id);
        return optionalEventSponsor.orElse(null);
    }

    @Override
    public List<EventSponsor> getAllEventSponsors() {
        return ieventSponsorRepository.findAll();
    }

  @Override
  public EventSponsor assignSponsorToEvent(Long sponsorId, Long eventId, String avantages) {
    Event event = eventRepository.findById(eventId).orElse(null);
    Sponsor sponsor = sponsorRepository.findById(sponsorId).orElse(null);

    if (event != null && sponsor != null) {

      EventSponsor eventSponsor = new EventSponsor();
      eventSponsor.setEvent(event);
      eventSponsor.setSponsor(sponsor);
      eventSponsor.setAvantages(avantages);

      return ieventSponsorRepository.save(eventSponsor);
    }
    return null;
  }
}

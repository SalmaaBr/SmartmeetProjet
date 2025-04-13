package tn.esprit.examen.Smartmeet.Services.GhanemRidene;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.Sponsor;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Event;
import tn.esprit.examen.Smartmeet.repositories.GhanemRiden.SponsorRepository;
import tn.esprit.examen.Smartmeet.repositories.SalmaBenRomdhan.IEventRepository;

import java.util.List;

@Service
public class SponsorServiceImpl implements SponsorService {

  @Autowired
  private IEventRepository eventRepository;

  @Autowired
  private SponsorRepository sponsorRepository;

  @Override
  public Sponsor createSponsor(Sponsor sponsor) {
    return sponsorRepository.save(sponsor);
  }

  @Override
  public Sponsor getSponsorById(Long id) {
    return sponsorRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("Sponsor non trouvé"));
  }

  @Override
  public List<Sponsor> getAllSponsors() {
    return sponsorRepository.findAll();
  }

  @Override
  public Sponsor updateSponsor(Sponsor sponsor) {
    getSponsorById(sponsor.getId()); // Vérifie si le sponsor existe
    return sponsorRepository.save(sponsor);
  }

  @Override
  public void deleteSponsor(Long id) {
    getSponsorById(id); // Vérifie si le sponsor existe
    sponsorRepository.deleteById(id);
  }

  @Override
  public void addEventToSponsor(Long sponsorId, Long eventId) {
    Sponsor sponsor = sponsorRepository.findById(sponsorId)
      .orElseThrow(() -> new RuntimeException("Sponsor non trouvé"));
    Event event = eventRepository.findById(eventId)
      .orElseThrow(() -> new RuntimeException("Événement non trouvé"));

    sponsor.getEvents().add(event);
    event.getSponsors().add(sponsor);

    sponsorRepository.save(sponsor);
    eventRepository.save(event);
  }
}

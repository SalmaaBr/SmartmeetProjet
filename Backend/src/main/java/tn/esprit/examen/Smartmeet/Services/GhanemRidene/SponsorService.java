package tn.esprit.examen.Smartmeet.Services.GhanemRidene;

import tn.esprit.examen.Smartmeet.entities.GhanemRidene.Sponsor;

import java.util.List;

public interface SponsorService {

  Sponsor createSponsor(Sponsor sponsor);

  Sponsor getSponsorById(Long id);

  List<Sponsor> getAllSponsors();

  Sponsor updateSponsor(Sponsor sponsor);

  void deleteSponsor(Long id);

  void addEventToSponsor(Long sponsorId, Long eventId);
}

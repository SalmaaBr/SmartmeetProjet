package tn.esprit.examen.Smartmeet.Services.GhanemRidene;

import tn.esprit.examen.Smartmeet.entities.GhanemRidene.Sponsor;
import java.util.List;

public interface ISponsorService {
    Sponsor addSponsor(Sponsor sponsor);
    Sponsor updateSponsor(Sponsor sponsor);
    void deleteSponsor(Long id);
    Sponsor getSponsorById(Long id);
    List<Sponsor> getAllSponsors();
} 
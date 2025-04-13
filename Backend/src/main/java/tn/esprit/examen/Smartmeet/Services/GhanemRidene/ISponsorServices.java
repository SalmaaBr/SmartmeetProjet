package tn.esprit.examen.Smartmeet.Services.GhanemRidene;


import tn.esprit.examen.Smartmeet.entities.GhanemRidene.Sponsor;

import java.util.List;

public interface ISponsorServices {
    Sponsor createSponsor(Sponsor sponsor);
    Sponsor updateSponsor(Long id, Sponsor sponsor);
    void deleteSponsor(Long id);
    Sponsor getSponsorById(Long id);
    List<Sponsor> getAllSponsors();

}

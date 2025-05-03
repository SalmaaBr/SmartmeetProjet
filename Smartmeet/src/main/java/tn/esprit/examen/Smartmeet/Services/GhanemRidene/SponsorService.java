package tn.esprit.examen.Smartmeet.Services.GhanemRidene;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.Sponsor;
import tn.esprit.examen.Smartmeet.repositories.GhanemRiden.SponsorRepository;

import java.util.List;

@Service
public class SponsorService implements ISponsorService {

    @Autowired
    private SponsorRepository sponsorRepository;

    @Override
    public Sponsor addSponsor(Sponsor sponsor) {
        return sponsorRepository.save(sponsor);
    }

    @Override
    public Sponsor updateSponsor(Sponsor sponsor) {
        if (!sponsorRepository.existsById(sponsor.getIdSponsor())) {
            throw new RuntimeException("Sponsor not found with id: " + sponsor.getIdSponsor());
        }
        return sponsorRepository.save(sponsor);
    }

    @Override
    public void deleteSponsor(Long id) {
        sponsorRepository.deleteById(id);
    }

    @Override
    public Sponsor getSponsorById(Long id) {
        return sponsorRepository.findById(id).orElse(null);
    }

    @Override
    public List<Sponsor> getAllSponsors() {
        return sponsorRepository.findAll();
    }
} 
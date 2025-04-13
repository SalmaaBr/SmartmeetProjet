package tn.esprit.examen.Smartmeet.Services.GhanemRidene;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.Sponsor;
import tn.esprit.examen.Smartmeet.repositories.GhanemRiden.ISponsorRepository;


import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service


public class SponsorServicesImpl implements ISponsorServices {


    private final ISponsorRepository sponsorRepository;

    @Override
    public Sponsor createSponsor(Sponsor sponsor) {
        return sponsorRepository.save(sponsor);
    }

    @Override
    public Sponsor updateSponsor(Long id, Sponsor sponsor) {
        sponsor.setId(id);
        return sponsorRepository.save(sponsor);
    }

    @Override
    public void deleteSponsor(Long id) {
        sponsorRepository.deleteById(id);
    }

    @Override
    public Sponsor getSponsorById(Long id) {
        Optional<Sponsor> optionalSponsor = sponsorRepository.findById(id);
        return optionalSponsor.orElse(null);
    }

    @Override
    public List<Sponsor> getAllSponsors() {
        return sponsorRepository.findAll();
    }
}



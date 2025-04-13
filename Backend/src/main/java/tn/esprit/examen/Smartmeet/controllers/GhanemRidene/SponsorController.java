package tn.esprit.examen.Smartmeet.controllers.GhanemRidene;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.Smartmeet.Services.GhanemRidene.SponsorService;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.Sponsor;

import java.util.List;

@RestController
@RequestMapping("/sponsors")
@CrossOrigin(origins = "http://localhost:4200")
public class SponsorController {

  @Autowired
  private SponsorService sponsorService;

  @PostMapping
  public Sponsor createSponsor(@RequestBody Sponsor sponsor) {
    return sponsorService.createSponsor(sponsor);
  }

  @GetMapping("/{id}")
  public Sponsor getSponsorById(@PathVariable Long id) {
    return sponsorService.getSponsorById(id);
  }

  @GetMapping
  public List<Sponsor> getAllSponsors() {
    return sponsorService.getAllSponsors();
  }

  @PutMapping("/{id}")
  public Sponsor updateSponsor(@PathVariable Long id, @RequestBody Sponsor sponsor) {
    sponsor.setId(id); // Assure que l’ID correspond
    return sponsorService.updateSponsor(sponsor);
  }

  @DeleteMapping("/{id}")
  public void deleteSponsor(@PathVariable Long id) {
    sponsorService.deleteSponsor(id);
  }
  @PostMapping("/{sponsorId}/events/{eventId}")
  public void addEventToSponsor(@PathVariable Long sponsorId, @PathVariable Long eventId) {
    sponsorService.addEventToSponsor(sponsorId, eventId);
  }
}

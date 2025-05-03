package tn.esprit.examen.Smartmeet.controllers.GhanemRidene;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.Smartmeet.Services.GhanemRidene.ISponsorService;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.Sponsor;

import java.util.List;

@RestController
@RequestMapping("/api/sponsors")
public class SponsorController {

    @Autowired
    private ISponsorService sponsorService;

    @PostMapping
    public ResponseEntity<Sponsor> addSponsor(@RequestBody Sponsor sponsor) {
        try {
            Sponsor savedSponsor = sponsorService.addSponsor(sponsor);
            return new ResponseEntity<>(savedSponsor, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping
    public ResponseEntity<Sponsor> updateSponsor(@RequestBody Sponsor sponsor) {
        try {
            Sponsor updatedSponsor = sponsorService.updateSponsor(sponsor);
            return new ResponseEntity<>(updatedSponsor, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSponsor(@PathVariable Long id) {
        try {
            sponsorService.deleteSponsor(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sponsor> getSponsorById(@PathVariable Long id) {
        try {
            Sponsor sponsor = sponsorService.getSponsorById(id);
            if (sponsor != null) {
                return new ResponseEntity<>(sponsor, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<Sponsor>> getAllSponsors() {
        try {
            List<Sponsor> sponsors = sponsorService.getAllSponsors();
            return new ResponseEntity<>(sponsors, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
} 
package tn.esprit.examen.Smartmeet.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.Smartmeet.Services.GhanemRidene.ISponsorServices;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.Sponsor;


import java.util.List;

@RequiredArgsConstructor
@RequestMapping("Sponsorship")
@RestController
@Tag(name="hello")


public class SponsorRestController {
    private final ISponsorServices sponsorServices;


    @PostMapping
    public ResponseEntity<Sponsor> createSponsor(@RequestBody Sponsor sponsor) {
        Sponsor createdSponsor = sponsorServices.createSponsor(sponsor);
        return new ResponseEntity<>(createdSponsor, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Sponsor> updateSponsor(@PathVariable Long id, @RequestBody Sponsor sponsor) {
        Sponsor updatedSponsor = sponsorServices.updateSponsor(id, sponsor);
        return new ResponseEntity<>(updatedSponsor, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSponsor(@PathVariable Long id) {
        sponsorServices.deleteSponsor(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sponsor> getSponsorById(@PathVariable Long id) {
        Sponsor sponsor = sponsorServices.getSponsorById(id);
        return sponsor != null ? new ResponseEntity<>(sponsor, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping
    public ResponseEntity<List<Sponsor>> getAllSponsors() {
        List<Sponsor> sponsors = sponsorServices.getAllSponsors();
        return new ResponseEntity<>(sponsors, HttpStatus.OK);
    }

}

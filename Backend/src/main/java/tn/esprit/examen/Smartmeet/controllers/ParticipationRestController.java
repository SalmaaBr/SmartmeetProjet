package tn.esprit.examen.Smartmeet.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.Smartmeet.Services.MaryemJeljli.IParticipationServices;
import tn.esprit.examen.Smartmeet.entities.MaryemJeljli.Participation;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("Participation")
@RestController
@Tag(name="hello")


public class ParticipationRestController {

    private final IParticipationServices participationServices;

    @PostMapping("/AddParticipation")
    public Participation addParticipation(@RequestBody Participation participation) {
        return participationServices.addParticipation(participation);
    }

    @GetMapping("/ReadParticipationByID/{id}")
    public Participation retrieveParticipation(@PathVariable int id) {
        return participationServices.retrieveParticipation(id);
    }

    @GetMapping("/ReadAllParticipations")
    public List<Participation> retrieveAllParticipations() {
        return participationServices.retrieveAllParticipations();
    }

    @DeleteMapping("/DeleteParticipationByID/{id}")
    public void deleteParticipation(@PathVariable int id) {
        participationServices.deleteParticipation(id);
    }

    @PutMapping("/UpdateParticipationByID/{id}")
    public void updateParticipation(@PathVariable int id, @RequestBody Participation participation) {
        participationServices.updateParticipation(id,participation);
    }
}

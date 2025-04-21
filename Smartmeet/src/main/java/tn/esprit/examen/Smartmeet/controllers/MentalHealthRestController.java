package tn.esprit.examen.Smartmeet.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.Smartmeet.Services.MaryemSalhi.MentalHealthServicesImpl;
import tn.esprit.examen.Smartmeet.entities.MaryemSalhi.MentalHealth;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("MentalHealth")
@RestController
@Tag(name="hello")

public class MentalHealthRestController {
    private final MentalHealthServicesImpl servicesMentalhealth;

    @PostMapping("/add-mentalhealth")
    public MentalHealth addMentalhealth(@RequestBody MentalHealth mentalhealth) {
        return servicesMentalhealth.addMentalhealth(mentalhealth);
    }

    @PutMapping("/update-mentalhealth")
    public MentalHealth updateMentalhealth(@RequestBody MentalHealth mentalhealth) {
        return servicesMentalhealth.updateMentalhealth(mentalhealth);
    }

    @DeleteMapping("/delete-mentalhealth/{id}")
    public void deleteMentalhealth(@PathVariable int id) {
        servicesMentalhealth.deleteMentalhealth(id);
    }

    @GetMapping("/get-mentalhealth/{id}")
    public MentalHealth getMentalhealthById(@PathVariable int id) {
        return servicesMentalhealth.getMentalhealthById(id);
    }

    @GetMapping("/get-all-mentalhealths")
    public List<MentalHealth> getAllMentalhealths() {
        return servicesMentalhealth.getAllMentalhealths();
    }


    @PostMapping("/add-mentalhealth-and-assign-to-user/{userId}")
    public MentalHealth addMentalHealthAndAssignToUser(@RequestBody MentalHealth mentalHealth, @PathVariable Long userId) {
        return servicesMentalhealth.addMentalHealthAndAssignToUser(mentalHealth, userId);
    }
}


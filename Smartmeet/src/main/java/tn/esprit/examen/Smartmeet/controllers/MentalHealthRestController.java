package tn.esprit.examen.Smartmeet.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.examen.Smartmeet.Services.MaryemSalhi.MentalHealthServicesImpl;
import tn.esprit.examen.Smartmeet.entities.MaryemSalhi.MentalHealth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("MentalHealth")
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@Tag(name="hello")
@RequiredArgsConstructor
@Slf4j


public class MentalHealthRestController {
    private final MentalHealthServicesImpl servicesMentalhealth;

    @PostMapping("/add-mentalhealth")
    public MentalHealth addMentalhealth(@RequestBody MentalHealth mentalhealth) {
        return servicesMentalhealth.addMentalhealth(mentalhealth);
    }

    @PutMapping("/update-mentalhealth/{id}")
    public MentalHealth updateMentalhealth(@PathVariable Long id, @RequestBody MentalHealth mentalhealth) {
        log.info("Received update request - ID: {}, MentalHealth: {}", id, mentalhealth);
        mentalhealth.setIdMentalHealth(id); // Assigner l'ID de l'URL à l'objet
        try {
            MentalHealth updatedMentalHealth = servicesMentalhealth.updateMentalhealth(mentalhealth);
            return updatedMentalHealth;
        } catch (IllegalArgumentException e) {
            log.error("Error updating mental health: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/delete-mentalhealth/{id}")
    public void deleteMentalhealth(@PathVariable Long id) {
        servicesMentalhealth.deleteMentalhealth(id);
    }

    @GetMapping("/get-mentalhealth/{id}")
    public MentalHealth getMentalhealthById(@PathVariable Long id) {
        return servicesMentalhealth.getMentalhealthById(id);
    }

    @GetMapping("/get-all-mentalhealths")
    public List<MentalHealth> getAllMentalhealths() {
        return servicesMentalhealth.getAllMentalhealths();
    }




    // Nouvel endpoint sécurisé pour l’utilisateur connecté
    @PostMapping("/submit-for-current-user")
    public ResponseEntity<Map<String, Object>> submitMentalHealthForCurrentUser(@RequestBody MentalHealth mentalHealth) {
        MentalHealth savedMentalHealth = servicesMentalhealth.addMentalHealthForCurrentUser(mentalHealth);

        // Préparer la réponse avec la notification et le résultat
        Map<String, Object> response = new HashMap<>();
        String notification = savedMentalHealth.getUser().getUsername() + " a soumis un formulaire de santé mentale.";
        response.put("notification", notification);
        response.put("result", savedMentalHealth); // Résultat envoyé à l’utilisateur

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/qrcode")
    public ResponseEntity<byte[]> generateQRCode(
            @PathVariable Long id,
            @RequestParam(defaultValue = "300") int width,
            @RequestParam(defaultValue = "300") int height) {
        try {
            byte[] qrCodeImage = servicesMentalhealth.generateQRCode(id, width, height); // Updated to use mentalHealthService

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(qrCodeImage.length);

            return new ResponseEntity<>(qrCodeImage, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}


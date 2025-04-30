package tn.esprit.examen.Smartmeet.controllers.MariamSalhi;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.examen.Smartmeet.Services.MaryemSalhi.MentalHealthServicesImpl;
import tn.esprit.examen.Smartmeet.entities.MaryemSalhi.MentalHealth;
import tn.esprit.examen.Smartmeet.entities.Users.Users;
import tn.esprit.examen.Smartmeet.repositories.Users.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequestMapping("MentalHealth")
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@Tag(name = "hello")
@RequiredArgsConstructor
@Slf4j
public class MentalHealthRestController {

    private final MentalHealthServicesImpl servicesMentalhealth;
    private final RestTemplate restTemplate = new RestTemplate();



    @PostMapping("/add-mentalhealth")
    public ResponseEntity<?> addMentalhealth(@RequestBody MentalHealth mentalhealth) {
        try {
            MentalHealth savedMentalHealth = servicesMentalhealth.addMentalhealth(mentalhealth);
            Users user = savedMentalHealth.getUser();
            if (user == null || user.getUserID() == null) {
                log.error("User information is missing in saved MentalHealth");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
            Long userId = user.getUserID();
            List<MentalHealth> submissions = servicesMentalhealth.getLastThreeSubmissionsByUser(userId);
            int submissionCount = submissions.size();
            if (submissionCount == 3) {
                ResponseEntity<Map<String, Object>> predictionResponse = predictMentalHealth(userId);
                if (predictionResponse.getStatusCode() == HttpStatus.OK && predictionResponse.getBody() != null) {
                    Map<String, Object> result = new HashMap<>();
                    result.put("mentalHealth", savedMentalHealth);
                    result.put("prediction", predictionResponse.getBody());

                    return new ResponseEntity<>(result, HttpStatus.CREATED);
                }
            }
            return new ResponseEntity<>(savedMentalHealth, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            log.error("Erreur lors de l'ajout de MentalHealth: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/update-mentalhealth/{id}")
    public MentalHealth updateMentalhealth(@PathVariable Long id, @RequestBody MentalHealth mentalhealth) {
        log.info("Received update request - ID: {}, MentalHealth: {}", id, mentalhealth);
        mentalhealth.setIdMentalHealth(id);
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

    @PostMapping("/submit-for-current-user")
    public ResponseEntity<Map<String, Object>> submitMentalHealthForCurrentUser(@RequestBody MentalHealth mentalHealth) {
        MentalHealth savedMentalHealth = servicesMentalhealth.addMentalHealthForCurrentUser(mentalHealth);
        Map<String, Object> response = new HashMap<>();
        String notification = savedMentalHealth.getUser().getUsername() + " a soumis un formulaire de santé mentale.";
        response.put("notification", notification);
        response.put("result", savedMentalHealth);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/qrcode")
    public ResponseEntity<byte[]> generateQRCode(
            @PathVariable Long id,
            @RequestParam(defaultValue = "300") int width,
            @RequestParam(defaultValue = "300") int height) {
        try {
            byte[] qrCodeImage = servicesMentalhealth.generateQRCode(id, width, height);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(qrCodeImage.length);
            return new ResponseEntity<>(qrCodeImage, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/predict-mentalhealth/{userId}")
    public ResponseEntity<Map<String, Object>> predictMentalHealth(@PathVariable Long userId) {
        try {
            // Récupérer les trois derniers formulaires
            List<MentalHealth> submissions = servicesMentalhealth.getLastThreeSubmissionsByUser(userId);
            if (submissions.size() < 3) {
                throw new IllegalStateException("L'utilisateur doit avoir soumis au moins trois formulaires.");
            }

            // Mapper les données pour l'API Python
            List<Map<String, Object>> entries = submissions.stream().map(mh -> {
                Map<String, Object> entry = new HashMap<>();
                entry.put("response_moment", mh.getResponseMoment().toString());
                entry.put("stress_level", mh.getStressLevel());
                entry.put("emotional_state", mh.getEmotionalState().toString());
                entry.put("support_need", mh.getSupportNeed().toString());
                return entry;
            }).collect(Collectors.toList());

            // Appeler l'API Flask
            String flaskUrl = "http://localhost:5001/predict";
            ResponseEntity<Map> response = restTemplate.postForEntity(flaskUrl, entries, Map.class);

            // Vérifier la réponse
            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new RuntimeException("Erreur lors de l'appel à l'API de prédiction.");
            }

            // Retourner la réponse
            Map<String, Object> result = new HashMap<>();
            result.put("crisis_detected", response.getBody().get("crisis_detected"));
            result.put("crisis_probability", response.getBody().get("crisis_probability"));
            result.put("message", response.getBody().get("message"));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Erreur lors de la prédiction : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
package tn.esprit.examen.Smartmeet.controllers.SalmaBenRomdhan;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;


@RestController
@RequestMapping("/api/map")
public class MapRoutingController {

    private final String ORS_API_KEY = "5b3ce3597851110001cf6248e66cfd5726c549b286e897c32a05c468";

    @PostMapping("/route")
    public ResponseEntity<?> getRoute(@RequestBody Map<String, Object> requestBody) {
        try {
            System.out.println("➡️ Requête reçue : " + requestBody);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", ORS_API_KEY);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            String orsUrl = "https://api.openrouteservice.org/v2/directions/driving-car/geojson";

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(
                    orsUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            System.out.println("✅ Réponse ORS : " + response.getBody());
            return ResponseEntity.ok(response.getBody());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récupération de l'itinéraire : " + e.getMessage());
        }
    }
}

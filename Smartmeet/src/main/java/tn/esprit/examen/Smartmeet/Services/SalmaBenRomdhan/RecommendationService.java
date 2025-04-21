package tn.esprit.examen.Smartmeet.Services.SalmaBenRomdhan;

// RecommendationService.java


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import tn.esprit.examen.Smartmeet.entities.Users.Users;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecommendationService {

    @Value("${python.api.url}")
    private String pythonApiUrl;

    private final RestTemplate restTemplate;

    public RecommendationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Map<String, Object>> getRecommendationsForCurrentUser(String authToken) {
        try {
            // 1. Configurer les headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authToken);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            // 2. Configurer la requête
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // 3. Exécuter l'appel
            ResponseEntity<Map> response = restTemplate.exchange(
                    pythonApiUrl + "/recommend/current-user",
                    HttpMethod.GET,
                    entity,
                    Map.class,
                    Collections.emptyMap());

            // 4. Traiter la réponse
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return (List<Map<String, Object>>) response.getBody().get("recommendations");
            }
            return Collections.emptyList();

        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Erreur d'appel au service Python: " + e.getStatusCode());
        } catch (Exception e) {
            throw new RuntimeException("Erreur interne: " + e.getMessage());
        }
    }

    public List<Map<String, Object>> getRecommendationsForUser(Long userId, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", authToken);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("user_id", userId);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                pythonApiUrl + "/recommend",
                HttpMethod.POST,
                entity,
                Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return (List<Map<String, Object>>) response.getBody().get("recommendations");
        }
        return Collections.emptyList();
    }
}
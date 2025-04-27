package tn.esprit.examen.Smartmeet.Services.MaryemJeljli;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

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
            // 1. Headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authToken);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            // 2. Requête
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // 3. Appel à Flask
            ResponseEntity<Map> response = restTemplate.exchange(
                    pythonApiUrl + "/recommend/current-user",
                    HttpMethod.GET,
                    entity,
                    Map.class,
                    Collections.emptyMap()
            );

            // 4. Traitement de la réponse
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
        try {
            // 1. Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", authToken);

            // 2. Body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("user_id", userId);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 3. Appel à Flask
            ResponseEntity<Map> response = restTemplate.exchange(
                    pythonApiUrl + "/recommend",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            // 4. Traitement de la réponse
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
}


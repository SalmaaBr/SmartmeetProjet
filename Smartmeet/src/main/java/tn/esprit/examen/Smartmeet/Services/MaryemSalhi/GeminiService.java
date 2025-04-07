package tn.esprit.examen.Smartmeet.Services.MaryemSalhi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private static final Logger logger = LoggerFactory.getLogger(GeminiService.class);

    @Value("${gemini.api.key}")
    private String apiKey;

    private final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";

    public String makeProfessional(String comment) {
        RestTemplate restTemplate = new RestTemplate();

        // Build the full URL with the API key appended
        String url = GEMINI_URL + "?key=" + apiKey;

        // Prepare the request body for the Gemini API with an enhanced instruction to generate a detailed response
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text",
                                        "Reformule ce commentaire de manière professionnelle et détaillée. " +
                                                "Ajoute une analyse complète, couvrant les aspects positifs et négatifs du cours, " +
                                                "et présente les informations de manière claire et argumentée. " +
                                                "Voici le commentaire : " + comment)
                        ))
                )
        );

        // Set up headers for the request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create the HTTP request entity
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            // Send the POST request to the Gemini API
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            logger.info("Gemini API response: " + response.getStatusCode());

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                    List<Map<String, String>> parts = (List<Map<String, String>>) content.get("parts");
                    String result = parts.get(0).get("text");
                    logger.info("Reformulated comment: " + result);
                    return result;
                }
            } else {
                logger.error("Error in Gemini API response: " + response.getBody());
            }
        } catch (Exception e) {
            logger.error("Error calling Gemini API: ", e);
        }

        return comment; // Return the original comment if something goes wrong
    }
}
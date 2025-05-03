package tn.esprit.examen.Smartmeet.Services.MaryemSalhi;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class SentimentAnalysisService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String apiUrl;
    private static final Set<String> VALID_SENTIMENTS = new HashSet<>(Arrays.asList(
            "POSITIVE", "HAPPY", "LOVE", "EXCITED", "SAD", "ANGRY",
            "AFRAID", "DISAPPOINTED", "CONFUSED", "SURPRISED", "NEUTRAL"
    ));
    private static final double CONFIDENCE_THRESHOLD = 0.5;

    public SentimentAnalysisService(
            @Value("${sentiment.api.url:http://localhost:8000/analyze-sentiment}") String apiUrl
    ) {
        this.apiUrl = apiUrl;
    }

    public String analyzeSentiment(String message) throws Exception {
        log.info("Analyzing sentiment for message: {}", message);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(Map.of("message", message));
        } catch (Exception e) {
            log.error("Erreur lors de la création du corps de la requête: {}", e.getMessage());
            throw new RuntimeException("Erreur lors de la préparation de la requête", e);
        }
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        String response;
        try {
            response = restTemplate.postForObject(apiUrl, request, String.class);
            log.info("Réponse brute de l'API: {}", response);
        } catch (RestClientException e) {
            log.error("Erreur lors de l'appel à l'API FastAPI: {}", e.getMessage());
            throw new RuntimeException("Erreur lors de l'appel à l'API FastAPI: " + e.getMessage(), e);
        }

        Map<String, Object> result;
        try {
            result = objectMapper.readValue(response, Map.class);
            log.info("Résultat parsé: {}", result);
        } catch (Exception e) {
            log.error("Erreur lors du parsing de la réponse: {}", e.getMessage());
            throw new RuntimeException("Erreur lors du parsing de la réponse de l'API", e);
        }

        if (result.containsKey("error")) {
            String errorMessage = String.valueOf(result.get("error"));
            log.error("Erreur retournée par l'API: {}", errorMessage);
            throw new RuntimeException("Erreur dans l'analyse du sentiment: " + errorMessage);
        }

        String sentiment = (String) result.get("sentiment");
        if (sentiment == null) {
            log.error("Aucun sentiment trouvé dans la réponse: {}", result);
            throw new RuntimeException("Aucun sentiment retourné par l'API");
        }

        if (!VALID_SENTIMENTS.contains(sentiment)) {
            log.warn("Sentiment non valide reçu de l'API: {}. Reclassé comme NEUTRAL.", sentiment);
            return "NEUTRAL";
        }

        Object confidenceObj = result.get("confidence");
        if (confidenceObj instanceof Number) {
            double confidence = ((Number) confidenceObj).doubleValue();
            if (confidence < CONFIDENCE_THRESHOLD) {
                log.warn("Confiance trop faible ({} < {}). Reclassé comme NEUTRAL.", confidence, CONFIDENCE_THRESHOLD);
                return "NEUTRAL";
            }
        } else {
            log.warn("Champ 'confidence' manquant ou invalide dans la réponse: {}", result);
        }

        log.info("Sentiment validé: {}", sentiment);
        return sentiment;
    }

    public Set<String> getValidSentiments() {
        return VALID_SENTIMENTS;
    }
}
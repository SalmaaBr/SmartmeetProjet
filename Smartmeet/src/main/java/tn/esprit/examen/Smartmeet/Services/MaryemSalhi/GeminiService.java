package tn.esprit.examen.Smartmeet.Services.MaryemSalhi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class GeminiService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String apiKey = "AIzaSyCFuor3YAig1FvWj39eEMiW6ERz4UjeoqU"; // à sécuriser ensuite

    public GeminiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://generativelanguage.googleapis.com")
                .build();
    }

    public String reformulateMessage(String originalMessage) {
        String prompt = "Please rewrite the following message as a professional and polished paragraph:\n" + originalMessage;

        String requestBody = """
        {
          "contents": [
            {
              "parts": [
                {
                  "text": "%s"
                }
              ]
            }
          ]
        }
        """.formatted(prompt);

        try {
            String response = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1beta/models/gemini-2.0-flash:generateContent")
                            .queryParam("key", apiKey)
                            .build())
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("Gemini raw response: {}", response);

            // Extraction du texte reformulé
            JsonNode root = objectMapper.readTree(response);
            JsonNode textNode = root.path("candidates").get(0).path("content").path("parts").get(0).path("text");

            if (!textNode.isMissingNode()) {
                return textNode.asText();
            } else {
                log.warn("No reformulated text found in Gemini response.");
                return originalMessage;
            }

        } catch (Exception e) {
            log.error("Error while calling Gemini API: {}", e.getMessage());
            return originalMessage;
        }
    }
}

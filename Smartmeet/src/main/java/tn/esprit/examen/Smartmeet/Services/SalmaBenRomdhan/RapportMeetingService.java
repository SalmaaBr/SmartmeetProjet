package tn.esprit.examen.Smartmeet.Services.SalmaBenRomdhan;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class RapportMeetingService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final WebClient webClient;

    public RapportMeetingService(@Value("${gemini.api.key}") String apiKey) {
        this.apiKey = apiKey;
        this.webClient = WebClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Mono<byte[]> genererRapportMeeting(String rawReport) {
        String prompt = "je veux que tu me donne un rapport bien structuré a partir du paragraphe que je vais te la donner sur ce qui c'est passé dans l'entretient entre rh et user :\n\n" + rawReport;

        return generateStructuredText(prompt)
                .flatMap(this::generatePdf);
    }

    public Mono<String> generateStructuredText(String prompt) {
        GeminiRequest request = new GeminiRequest(
                new Content[]{
                        new Content(new Part[]{
                                new Part(prompt)
                        })
                }
        );

        return webClient.post()
                .uri("/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .map(response -> {
                    if (response.candidates() != null && response.candidates().length > 0) {
                        return response.candidates()[0].content().parts()[0].text();
                    }
                    throw new RuntimeException("No response from Gemini API");
                });
    }

    private Mono<byte[]> generatePdf(String content) {
        return Mono.fromCallable(() -> {
            try (PDDocument document = new PDDocument()) {
                PDPage page = new PDPage();
                document.addPage(page);

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA, 12);
                    contentStream.setLeading(14.5f);
                    contentStream.newLineAtOffset(50, 700);

                    String[] lines = content.split("\n");
                    for (String line : lines) {
                        if (line.length() > 100) {
                            String[] words = line.split(" ");
                            StringBuilder currentLine = new StringBuilder();
                            for (String word : words) {
                                if (currentLine.length() + word.length() < 100) {
                                    currentLine.append(word).append(" ");
                                } else {
                                    contentStream.showText(currentLine.toString());
                                    contentStream.newLine();
                                    currentLine = new StringBuilder(word + " ");
                                }
                            }
                            contentStream.showText(currentLine.toString());
                        } else {
                            contentStream.showText(line);
                        }
                        contentStream.newLine();
                    }

                    contentStream.endText();
                }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                document.save(byteArrayOutputStream);
                return byteArrayOutputStream.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException("Erreur lors de la génération du PDF", e);
            }
        });
    }

    // Classes record pour la requête et la réponse Gemini
    record GeminiRequest(Content[] contents) {}
    record Content(Part[] parts) {}
    record Part(String text) {}
    record GeminiResponse(Candidate[] candidates) {}
    record Candidate(Content content) {}
}
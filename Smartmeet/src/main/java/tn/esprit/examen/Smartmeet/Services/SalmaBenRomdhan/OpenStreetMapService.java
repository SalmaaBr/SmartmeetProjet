package tn.esprit.examen.Smartmeet.Services.SalmaBenRomdhan;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.Map;

@Service
public class OpenStreetMapService {

    private final WebClient webClient;

    public OpenStreetMapService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://nominatim.openstreetmap.org")
                .defaultHeader("User-Agent", "Your Application Name")
                .build();
    }

    public Mono<String> searchLocation(String query) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("q", query)
                        .queryParam("format", "json")
                        .queryParam("limit", "5")
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> reverseGeocode(double lat, double lon) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/reverse")
                        .queryParam("lat", lat)
                        .queryParam("lon", lon)
                        .queryParam("format", "json")
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }
}

package tn.esprit.examen.Smartmeet.controllers.SalmaBenRomdhan;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.Smartmeet.Services.SalmaBenRomdhan.RecommendationService;
import tn.esprit.examen.Smartmeet.security.jwt.JwtUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/recommendations")

public class RecommendationController {

    private final RecommendationService recommendationService;
    private final JwtUtils jwtUtils;

    public RecommendationController(RecommendationService recommendationService,
                                    JwtUtils jwtUtils) {
        this.recommendationService = recommendationService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> getRecommendationsForCurrentUser(HttpServletRequest request) {
        try {
            // 1. Extraire le token JWT correctement
            String jwt = jwtUtils.getJwtFromHeader(request);
            if (jwt == null || !jwtUtils.validateJwtToken(jwt)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // 2. Appeler le service de recommandation
            List<Map<String, Object>> recommendations =
                    recommendationService.getRecommendationsForCurrentUser("Bearer " + jwt);

            // 3. Retourner la réponse
            return ResponseEntity.ok()
                    .body(recommendations);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getRecommendationsForUser(
            @PathVariable Long userId,
            Authentication authentication) {
        String authToken = extractToken(authentication);
        List<Map<String, Object>> recommendations = recommendationService.getRecommendationsForUser(userId, authToken);
        return ResponseEntity.ok(recommendations);
    }

    private String extractToken(Authentication authentication) {
        // Implémentez la logique pour extraire le token JWT de l'authentification
        // Cela dépend de votre configuration Spring Security
        return "Bearer " + authentication.getCredentials().toString();
    }
}

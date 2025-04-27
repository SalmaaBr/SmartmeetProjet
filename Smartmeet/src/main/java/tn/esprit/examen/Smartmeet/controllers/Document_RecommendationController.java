package tn.esprit.examen.Smartmeet.controllers;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.Smartmeet.Services.MaryemJeljli.RecommendationService;
import tn.esprit.examen.Smartmeet.security.jwt.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recommendations")
public class Document_RecommendationController {
    private final RecommendationService recommendationService;
    private final JwtUtils jwtUtils;
    private static final Logger logger = LoggerFactory.getLogger(RecommendationService.class);

    public Document_RecommendationController(RecommendationService recommendationService, JwtUtils jwtUtils) {
        this.recommendationService = recommendationService;
        this.jwtUtils = jwtUtils;
    }


    @GetMapping("/current-user")
    public ResponseEntity<?> getRecommendationsForCurrentUser(HttpServletRequest request) {
        try {
            String jwt = jwtUtils.getJwtFromHeader(request);
            if (jwt == null || !jwtUtils.validateJwtToken(jwt)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            List<Map<String, Object>> recommendations =
                    recommendationService.getRecommendationsForCurrentUser("Bearer " + jwt);

            return ResponseEntity.ok()
                    .body(recommendations);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping("/user/{userID}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getRecommendationsForUser(
            @PathVariable Long userID,
            HttpServletRequest request) {
        logger.info("Requête pour obtenir les recommandations pour l'utilisateur {}", userID);
        try {
            String jwt = jwtUtils.getJwtFromHeader(request);
            if (jwt == null || !jwtUtils.validateJwtToken(jwt)) {
                logger.warn("Token JWT invalide ou absent.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Collections.singletonMap("error", "Token invalide ou absent"));
            }

            String authToken = "Bearer " + jwt;
            List<Map<String, Object>> recommendations = recommendationService.getRecommendationsForUser(userID, authToken);

            logger.info("Recommandations récupérées pour l'utilisateur {} : {}", userID, recommendations);
            return ResponseEntity.ok(recommendations);

        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des recommandations pour l'utilisateur {} : {}", userID, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    private String extractToken(Authentication authentication) {
        Object credentials = authentication.getCredentials();
        if (credentials == null) {
            logger.error("Credentials are null in Authentication object");
            throw new IllegalStateException("Authentication credentials are missing");
        }
        return "Bearer " + credentials.toString();
    }
}

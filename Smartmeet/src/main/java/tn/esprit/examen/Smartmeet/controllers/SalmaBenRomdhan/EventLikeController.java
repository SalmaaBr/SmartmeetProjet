package tn.esprit.examen.Smartmeet.controllers.SalmaBenRomdhan;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.Smartmeet.Services.SalmaBenRomdhan.EventLikeService;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.ErrorResponse;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.EventLike;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.EventLikeDTO;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.SuccessResponse;
import tn.esprit.examen.Smartmeet.repositories.SalmaBenRomdhan.EventLikeRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/event-likes")
@RequiredArgsConstructor
public class EventLikeController {

    private final EventLikeService eventLikeService;
    private final EventLikeRepository eventLikeRepository;

    @PostMapping("/toggle/{eventId}")
    public ResponseEntity<?> toggleLike(@PathVariable Long eventId, Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body(new ErrorResponse("User not authenticated"));
            }
            String message = eventLikeService.toggleLike(eventId);
            return ResponseEntity.ok(new SuccessResponse(message));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ErrorResponse("Server error: " + e.getMessage()));
        }
    }

    @GetMapping("/status/{eventId}")
    public ResponseEntity<?> getLikeStatus(@PathVariable Long eventId, Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body(new ErrorResponse("User not authenticated"));
            }
            int status = eventLikeService.getLikeStatus(eventId);
            return ResponseEntity.ok(new SuccessResponse(status));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ErrorResponse("Server error: " + e.getMessage()));
        }
    }

    @GetMapping("/total/{eventId}")
    public ResponseEntity<?> getTotalLikes(@PathVariable Long eventId) {
        try {
            long totalLikes = eventLikeService.getTotalLikes(eventId);
            return ResponseEntity.ok(new SuccessResponse(totalLikes));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ErrorResponse("Server error: " + e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<EventLikeDTO>> getAllEventLikes() {
        List<EventLikeDTO> likes = eventLikeRepository.findAll().stream()
                .map(eventLike -> new EventLikeDTO(
                        eventLike.getId(),
                        eventLike.getUser().getUserID(),
                        eventLike.getEvent().getId(),
                        eventLike.getLikes()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(likes);
    }
}




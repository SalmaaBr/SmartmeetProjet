package tn.esprit.examen.Smartmeet.controllers.SalmaBenRomdhan;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.Smartmeet.Services.SalmaBenRomdhan.EventLikeService;

@RestController
@RequestMapping("/api/event-likes")
@RequiredArgsConstructor
public class EventLikeController {

    private final EventLikeService eventLikeService;

    @PostMapping("/toggle/{eventId}")
    public ResponseEntity<String> toggleLike(@PathVariable Long eventId) {
        String message = eventLikeService.toggleLike(eventId);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/status/{userId}/{eventId}")
    public ResponseEntity<Integer> getLikeStatus(@PathVariable Long userId, @PathVariable Long eventId) {
        int status = eventLikeService.getLikeStatus(userId, eventId);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/total/{eventId}")
    public ResponseEntity<Long> getTotalLikes(@PathVariable Long eventId) {
        long totalLikes = eventLikeService.getTotalLikes(eventId);
        return ResponseEntity.ok(totalLikes);
    }
}

package tn.esprit.examen.Smartmeet.controllers.SalmaBenRomdhan;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.Smartmeet.Services.SalmaBenRomdhan.EventLikeService;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.EventLike;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.EventLikeDTO;
import tn.esprit.examen.Smartmeet.repositories.SalmaBenRomdhan.EventLikeRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/event-likes")
@RequiredArgsConstructor
public class EventLikeController {

    private final EventLikeService eventLikeService;
    @Autowired
    private EventLikeRepository eventLikeRepository;

    @PostMapping("/toggle/{eventId}")
    public ResponseEntity<String> toggleLike(@PathVariable Long eventId) {
        String message = eventLikeService.toggleLike(eventId);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/status/{eventId}")
    public ResponseEntity<Integer> getLikeStatus( @PathVariable Long eventId) {
        int status = eventLikeService.getLikeStatus(eventId);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/total/{eventId}")
    public ResponseEntity<Long> getTotalLikes(@PathVariable Long eventId) {
        long totalLikes = eventLikeService.getTotalLikes(eventId);
        return ResponseEntity.ok(totalLikes);
    }

    @GetMapping("/all")
    public List<EventLikeDTO> getAllEventLikes() {
        return eventLikeRepository.findAll().stream()
                .map(eventLike -> new EventLikeDTO(
                        eventLike.getId(),
                        eventLike.getUser().getUserID(),
                        eventLike.getEvent().getId(),
                        eventLike.getLikes()
                ))
                .collect(Collectors.toList());
    }

}
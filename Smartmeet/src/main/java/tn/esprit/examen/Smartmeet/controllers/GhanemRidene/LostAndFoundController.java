package tn.esprit.examen.Smartmeet.controllers.GhanemRidene;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.Smartmeet.Services.GhanemRidene.LostAndFoundService;
import tn.esprit.examen.Smartmeet.Services.GhanemRidene.NotificationService;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.dto.LostAndFoundRequest;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.dto.LostAndFoundResponse;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.dto.NotificationDto;
import tn.esprit.examen.Smartmeet.security.services.UserDetailsImpl;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/lost-found")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LostAndFoundController {

    private final LostAndFoundService lostAndFoundService;
    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<LostAndFoundResponse> createLostAndFound(@Valid @RequestBody LostAndFoundRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = Long.parseLong(userDetails.getId());

        LostAndFoundResponse response = lostAndFoundService.createLostAndFound(request, userId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<LostAndFoundResponse>> getAllByEvent(@PathVariable Long eventId) {
        List<LostAndFoundResponse> items = lostAndFoundService.getAllLostAndFoundByEvent(eventId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LostAndFoundResponse> getById(@PathVariable Long id) {
        LostAndFoundResponse item = lostAndFoundService.getLostAndFoundById(id);
        return ResponseEntity.ok(item);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LostAndFoundResponse> updateLostAndFound(
            @PathVariable Long id,
            @Valid @RequestBody LostAndFoundRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = Long.parseLong(userDetails.getId());

        LostAndFoundResponse response = lostAndFoundService.updateLostAndFound(id, request, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLostAndFound(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = Long.parseLong(userDetails.getId());

        lostAndFoundService.deleteLostAndFound(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationDto>> getUserNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = Long.parseLong(userDetails.getId());

        List<NotificationDto> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/notifications/{id}/read")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}

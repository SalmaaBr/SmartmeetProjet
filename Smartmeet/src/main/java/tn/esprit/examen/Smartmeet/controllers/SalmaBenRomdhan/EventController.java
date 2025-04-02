package tn.esprit.examen.Smartmeet.controllers.SalmaBenRomdhan;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Event;
import tn.esprit.examen.Smartmeet.Services.SalmaBenRomdhan.IEventServices;
import org.springframework.beans.factory.annotation.Value;
import tn.esprit.examen.Smartmeet.repositories.SalmaBenRomdhan.IEventRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;



import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@CrossOrigin("*")
@RequiredArgsConstructor
@RequestMapping("/event")
@RestController
public class EventController {

    private final IEventServices eventService;

    private final IEventRepository eventRepository;

    @PostMapping(value = "/createevent", consumes = { "multipart/form-data" })
    public Event createEvent(@RequestPart("event") Event event, @RequestPart("file") MultipartFile file) {
        try {
            String uploadDir = "C:/Users/benro/Desktop/uploads";
            String fileName = file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);

            file.transferTo(filePath.toFile());

            // Formater le chemin du fichier en remplaçant les backslashes par des slashes
            event.setFilePath(filePath.toString().replace("\\", "/"));

            // Stocker seulement le nom du fichier, pas le chemin complet
            event.setFilePath(fileName);

            return eventService.createEvent(event);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'upload du fichier : " + e.getMessage());
        }
    }


    @PutMapping("updateevent/{id}")
    public Event updateEvent(@PathVariable Long id, @RequestBody Event event) {
        return eventService.updateEvent(id, event);
    }

    @DeleteMapping("deleteevent/{id}")
    public void deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
    }

    @GetMapping("getevent/{id}")
    public Event getEventById(@PathVariable Long id) {
        return eventService.getEventById(id);
    }

    @GetMapping("/getallevent")
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }


    @PostMapping("/assign/{userId}/{eventId}")
    public ResponseEntity<String> assignEventToUser(@PathVariable Long userId, @PathVariable Long eventId) {
        eventService.addAndAssignEventToUser(userId, eventId);
        return ResponseEntity.ok("Événement assigné avec succès !");
    }

    @GetMapping("/image/{fileName}")
    public ResponseEntity<byte[]> getImage(@PathVariable String fileName) {
        try {
            Path path = Paths.get("C:/Users/benro/Desktop/uploads", fileName);
            byte[] image = Files.readAllBytes(path);
            String contentType = Files.probeContentType(path);

            return ResponseEntity.ok()
                    .header("Content-Type", contentType != null ? contentType : "image/jpeg")
                    .body(image);
        } catch (IOException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    @GetMapping("/check-recruitment/{title}")
    public boolean checkEventHasRecruitment(@PathVariable String title) {
        Optional<Event> eventOpt = eventRepository.findByTitle(title);
        return eventOpt.isPresent() && eventOpt.get().getMonitorungrecutement() != null;
    }



}
package tn.esprit.examen.Smartmeet.controllers.SalmaBenRomdhan;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
import java.util.UUID;
import java.util.stream.Stream;

@CrossOrigin("*")
@RequiredArgsConstructor
@RequestMapping("/event")
@RestController
public class EventController {

    private final IEventServices eventService;

    private final IEventRepository eventRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @PostMapping(value = "/createevent", consumes = { "multipart/form-data" })
    public ResponseEntity<Event> createEvent(@RequestPart("event") Event event,
                                             @RequestPart("file") MultipartFile file) {
        try {
            // Créer le répertoire s'il n'existe pas
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Générer un nom de fichier unique
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            // Sauvegarder le fichier
            Files.copy(file.getInputStream(), filePath);

            // Stocker seulement le nom du fichier dans la base de données
            event.setFilePath(fileName);

            Event savedEvent = eventService.createEvent(event);
            return ResponseEntity.ok(savedEvent);
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

    @GetMapping(value = "/images/{fileName:.+}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImage(@PathVariable String fileName) {
        try {
            // Obtenez le chemin absolu à partir du chemin relatif
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path filePath = uploadPath.resolve(fileName);

            System.out.println("Chemin de recherche: " + filePath);
            System.out.println("Le dossier upload existe ? " + Files.exists(uploadPath));

            if (!Files.exists(filePath)) {
                System.out.println("Fichier introuvable. Contenu du dossier:");
                try (Stream<Path> paths = Files.list(uploadPath)) {
                    paths.forEach(System.out::println);
                }
                return ResponseEntity.notFound().build();
            }

            byte[] imageBytes = Files.readAllBytes(filePath);
            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .body(imageBytes);
        } catch (Exception e) {
            System.out.println("ERREUR: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/check-recruitment/{title}")
    public boolean checkEventHasRecruitment(@PathVariable String title) {
        Optional<Event> eventOpt = eventRepository.findByTitle(title);
        return eventOpt.isPresent() && eventOpt.get().getMonitorungrecutement() != null;
    }



}
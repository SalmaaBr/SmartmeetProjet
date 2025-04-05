package tn.esprit.examen.Smartmeet.controllers.SalmaBenRomdhan;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.examen.Smartmeet.Services.SalmaBenRomdhan.OpenStreetMapService;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Event;
import tn.esprit.examen.Smartmeet.Services.SalmaBenRomdhan.IEventServices;
import org.springframework.beans.factory.annotation.Value;
import tn.esprit.examen.Smartmeet.repositories.SalmaBenRomdhan.IEventRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;



import java.io.File;
import java.io.IOException;
import java.util.*;
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
                                             @RequestPart("file") MultipartFile file, BindingResult result) {
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


    @PutMapping(value = "/updateevent/{id}", consumes = { "multipart/form-data" })
    public Event updateEvent(@PathVariable Long id,
                             @RequestPart("event") Event event,
                             @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        // Récupérer l'événement existant
        Event existingEvent = eventService.getEventById(id);

        // Mettre à jour les champs
        existingEvent.setTitle(event.getTitle());
        existingEvent.setDescription(event.getDescription());
        existingEvent.setLocation(event.getLocation());
        existingEvent.setTypeevent(event.getTypeevent());
        existingEvent.setTypetheme(event.getTypetheme());
        existingEvent.setTypeweather(event.getTypeweather());
        existingEvent.setStartTime(event.getStartTime());
        existingEvent.setEndTime(event.getEndTime());
        existingEvent.setMaxParticipants(event.getMaxParticipants());

        // Si un nouveau fichier est fourni
        if (file != null && !file.isEmpty()) {
            // Supprimer l'ancien fichier s'il existe
            if (existingEvent.getFilePath() != null) {
                Path oldFilePath = Paths.get(uploadDir).resolve(existingEvent.getFilePath());
                Files.deleteIfExists(oldFilePath);
            }

            // Sauvegarder le nouveau fichier
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir).resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            existingEvent.setFilePath(fileName);
        }

        return eventService.updateEvent(id, existingEvent);
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


    @PostMapping("/evenements/{eventId}/participer")
    public ResponseEntity<?> participer(@PathVariable Long eventId) {
        try {
            int newMaxParticipants = eventService.addAndAssignEventToUser(eventId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Participation enregistrée !");
            response.put("maxParticipants", newMaxParticipants);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
        }
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

    // Ajoutez ces nouvelles méthodes dans votre contrôleur
    @Autowired
    private OpenStreetMapService openStreetMapService;

    @GetMapping("/search-location")
    public ResponseEntity<String> searchLocation(@RequestParam String query) {
        String result = openStreetMapService.searchLocation(query).block();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/reverse-geocode")
    public ResponseEntity<String> reverseGeocode(@RequestParam double lat, @RequestParam double lon) {
        String result = openStreetMapService.reverseGeocode(lat, lon).block();
        return ResponseEntity.ok(result);
    }



}
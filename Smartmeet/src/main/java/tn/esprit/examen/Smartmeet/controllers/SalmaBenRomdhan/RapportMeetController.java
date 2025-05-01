package tn.esprit.examen.Smartmeet.controllers.SalmaBenRomdhan;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import tn.esprit.examen.Smartmeet.Services.SalmaBenRomdhan.RapportMeetingService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/rapport-meetings")
public class RapportMeetController {

    private final RapportMeetingService rapportMeetingService;
    // In-memory storage for generated PDFs (temporary, consider a proper storage solution for production)
    private final Map<Long, byte[]> pdfStorage = new ConcurrentHashMap<>();

    public RapportMeetController(RapportMeetingService rapportMeetingService) {
        this.rapportMeetingService = rapportMeetingService;
    }

    @PostMapping("/{meetingId}/generer")
    public Mono<ResponseEntity<Resource>> genererRapportMeeting(
            @PathVariable Long meetingId,
            @RequestBody String rawReport) {

        return rapportMeetingService.genererRapportMeeting(rawReport)
                .map(pdfBytes -> {
                    // Store the PDF in memory for later download
                    pdfStorage.put(meetingId, pdfBytes);
                    Resource resource = new ByteArrayResource(pdfBytes); // Explicitly declare as Resource

                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION,
                                    ContentDisposition.attachment()
                                            .filename("rapport-meeting-" + meetingId + ".pdf")
                                            .build()
                                            .toString())
                            .contentType(MediaType.APPLICATION_PDF)
                            .body(resource);
                })
                .onErrorResume(e -> Mono.just(
                        ResponseEntity.badRequest()
                                .body(new ByteArrayResource(("Erreur lors de la génération du PDF: " + e.getMessage()).getBytes()))
                ));
    }

    @GetMapping("/{meetingId}/download")
    public Mono<ResponseEntity<Resource>> downloadRapportMeeting(@PathVariable Long meetingId) {
        return Mono.justOrEmpty(pdfStorage.get(meetingId))
                .map(pdfBytes -> {
                    Resource resource = new ByteArrayResource(pdfBytes); // Explicitly declare as Resource
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION,
                                    ContentDisposition.attachment()
                                            .filename("rapport-meeting-" + meetingId + ".pdf")
                                            .build()
                                            .toString())
                            .contentType(MediaType.APPLICATION_PDF)
                            .body(resource);
                })
                .switchIfEmpty(Mono.just(
                        ResponseEntity.notFound().build()
                ));
    }
}
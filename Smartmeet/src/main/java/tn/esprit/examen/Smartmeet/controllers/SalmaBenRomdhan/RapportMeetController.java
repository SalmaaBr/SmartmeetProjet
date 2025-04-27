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

@RestController
@RequestMapping("/api/rapport-meetings")
public class RapportMeetController {

    private final RapportMeetingService rapportMeetingService;

    public RapportMeetController(RapportMeetingService rapportMeetingService) {
        this.rapportMeetingService = rapportMeetingService;
    }

    @PostMapping("/{meetingId}/generer")
    public Mono<ResponseEntity<Resource>> genererRapportMeeting(
            @PathVariable Long meetingId,
            @RequestBody String rawReport) {

        return rapportMeetingService.genererRapportMeeting(rawReport)
                .map(pdfBytes -> {
                    ByteArrayResource resource = new ByteArrayResource(pdfBytes);

                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION,
                                    ContentDisposition.attachment()
                                            .filename("rapport-meeting-" + meetingId + ".pdf")
                                            .build()
                                            .toString())
                            .contentType(MediaType.APPLICATION_PDF)
                            .body(resource);
                });
    }
}

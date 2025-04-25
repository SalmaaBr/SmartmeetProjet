package tn.esprit.examen.Smartmeet.controllers.SalmaBenRomdhan;

import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.Smartmeet.Services.SalmaBenRomdhan.MeetingService;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Meeting;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.MeetingRequest;

import java.util.List;
import java.util.UUID;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/meetings")
public class MeetingController {

    private final MeetingService meetingService;

    public MeetingController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @PostMapping("/interview")
    public Meeting createInterview(@RequestBody MeetingRequest request) {
        return meetingService.createInterview(request);
    }

    @GetMapping("/{id}")
    public Meeting getMeeting(@PathVariable Long id) {
        return meetingService.getMeeting(id);
    }

    @PostMapping("/assign")
    public Meeting addAndAssignMeetingToUser(@RequestBody Meeting meeting,
                                             @RequestParam Long participantId) {
        return meetingService.addAndAssignMeetingToUser(meeting, participantId);
    }

    @GetMapping("/generate-room")
    public String generateRoom() {
        return UUID.randomUUID().toString();
    }

    // Nouvel endpoint pour récupérer les réunions de l'utilisateur connecté
    @GetMapping("/user-meetings")
    public List<Meeting> getUserMeetings() {
        return meetingService.getUserMeetings();
    }
}
package tn.esprit.examen.Smartmeet.controllers.SalmaBenRomdhan;


import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.Smartmeet.Services.SalmaBenRomdhan.MeetingService;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Meeting;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.MeetingRequest;

import java.util.UUID;

@RestController
@RequestMapping("/api/meetings")
public class MeetingController {

    private final MeetingService meetingService;

    public MeetingController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @PostMapping
    public Meeting createMeeting(@RequestBody MeetingRequest request) {
        return meetingService.createMeeting(request.getMeetingName());
    }

    @GetMapping("/{id}")
    public Meeting getMeeting(@PathVariable Long id) {
        return meetingService.getMeeting(id);
    }

    @GetMapping("/generate-room")
    public String generateRoom() {
        return UUID.randomUUID().toString();
    }

}


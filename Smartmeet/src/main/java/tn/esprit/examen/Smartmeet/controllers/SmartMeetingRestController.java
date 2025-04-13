package tn.esprit.examen.Smartmeet.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.Smartmeet.Services.YousraFourati.ISmartMeetingServices;
import tn.esprit.examen.Smartmeet.entities.MaryemJeljli.Participation;
import tn.esprit.examen.Smartmeet.entities.YousraFourati.SmartMeeting;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RequestMapping("SmartMeeting")
@RestController
@Tag(name="hello")

public class SmartMeetingRestController {
    private final ISmartMeetingServices smartMeetingServices;

    @GetMapping
    public List<SmartMeeting> getAllSmartMeetings() {
        return smartMeetingServices.getAllSmartMeetings();
    }

    @GetMapping("/{id}")
    public SmartMeeting getSmartMeetingById(@PathVariable int id) {
        return smartMeetingServices.getSmartMeetingById(id);
    }

    @PostMapping
    public SmartMeeting createSmartMeeting(@RequestBody SmartMeeting smartMeeting) {
        return smartMeetingServices.saveSmartMeeting(smartMeeting);
    }

    @PutMapping("/{id}")
    public SmartMeeting updateSmartMeeting(@PathVariable int id, @RequestBody SmartMeeting smartMeeting) {
        SmartMeeting existingMeeting = smartMeetingServices.getSmartMeetingById(id);
        if (existingMeeting != null) {
            smartMeeting.setSmartmeetingID(id);
            return smartMeetingServices.saveSmartMeeting(smartMeeting);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteSmartMeeting(@PathVariable int id) {
        smartMeetingServices.deleteSmartMeeting(id);
    }
    @PostMapping("/{smartMeetingId}/participations")
    public SmartMeeting assignParticipationToSmartMeeting(@PathVariable int smartMeetingId, @RequestBody Set<Participation> participations) {
        return smartMeetingServices.assignParticipationToSmartMeeting(smartMeetingId, participations);
    }
}

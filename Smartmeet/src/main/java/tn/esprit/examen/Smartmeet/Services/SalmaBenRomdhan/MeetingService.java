package tn.esprit.examen.Smartmeet.Services.SalmaBenRomdhan;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Meeting;
import tn.esprit.examen.Smartmeet.repositories.SalmaBenRomdhan.MeetingRepository;

import java.util.UUID;

@Service
public class MeetingService {

    @Value("${jitsi.domain:https://meet.jit.si}")
    private String jitsiDomain;

    private final MeetingRepository meetingRepository;

    public MeetingService(MeetingRepository meetingRepository) {
        this.meetingRepository = meetingRepository;
    }

    public Meeting createMeeting(String meetingName) {
        String roomId = UUID.randomUUID().toString(); // Générer un ID unique
        String meetingLink = jitsiDomain + "/" + roomId;

        Meeting meeting = new Meeting();
        meeting.setMeetingName(meetingName);
        meeting.setMeetingLink(meetingLink);

        return meetingRepository.save(meeting);
    }

    public Meeting getMeeting(Long id) {
        return meetingRepository.findById(id).orElse(null);
    }
}

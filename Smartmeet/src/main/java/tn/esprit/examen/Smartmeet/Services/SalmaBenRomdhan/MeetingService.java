package tn.esprit.examen.Smartmeet.Services.SalmaBenRomdhan;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Meeting;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.MeetingRequest;
import tn.esprit.examen.Smartmeet.entities.Users.Users;
import tn.esprit.examen.Smartmeet.repositories.SalmaBenRomdhan.MeetingRepository;
import tn.esprit.examen.Smartmeet.repositories.Users.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MeetingService {

    @Value("${jitsi.domain:https://meet.jit.si}")
    private String jitsiDomain;

    private final MeetingRepository meetingRepository;
    private final AvailabilityService availabilityService;
    private final UserRepository userRepository;

    public MeetingService(MeetingRepository meetingRepository,
                          AvailabilityService availabilityService,
                          UserRepository userRepository) {
        this.meetingRepository = meetingRepository;
        this.availabilityService = availabilityService;
        this.userRepository = userRepository;
    }

    public Meeting createInterview(MeetingRequest request) {
        // Récupérer l'utilisateur connecté
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Users organizer = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Organizer not found"));

        Users participant = userRepository.findById(request.getParticipantId())
                .orElseThrow(() -> new RuntimeException("Participant not found"));

        int duration = request.getDurationMinutes() != null ? request.getDurationMinutes() : 5;
        LocalDateTime startTime = availabilityService.findNextAvailableSlot(
                request.getParticipantId(),
                duration
        );
        LocalDateTime endTime = startTime.plusMinutes(duration);

        String roomId = UUID.randomUUID().toString();
        String meetingLink = jitsiDomain + "/" + roomId;

        Meeting meeting = new Meeting();
        meeting.setMeetingName(request.getMeetingName());
        meeting.setMeetingLink(meetingLink);
        meeting.setStartTime(startTime);
        meeting.setEndTime(endTime);
        meeting.setOrganizer(organizer);
        meeting.setParticipant(participant);

        return meetingRepository.save(meeting);
    }

    public Meeting getMeeting(Long id) {
        return meetingRepository.findById(id).orElse(null);
    }

    public Meeting addAndAssignMeetingToUser(Meeting meeting, Long participantId) {
        // Récupérer l'utilisateur connecté comme organisateur
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Users organizer = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Organizer not found"));

        Users participant = userRepository.findById(participantId)
                .orElseThrow(() -> new RuntimeException("Participant not found"));

        meeting.setOrganizer(organizer);
        meeting.setParticipant(participant);

        return meetingRepository.save(meeting);
    }

    // Nouvelle méthode pour récupérer les réunions de l'utilisateur connecté
    public List<Meeting> getUserMeetings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return meetingRepository.findByOrganizerOrParticipant(user, user);
    }

    public List<Meeting> getMeetingsByUserId(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return meetingRepository.findByOrganizerOrParticipant(user, user);
    }
}
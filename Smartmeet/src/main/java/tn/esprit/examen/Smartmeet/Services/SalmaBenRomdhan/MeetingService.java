package tn.esprit.examen.Smartmeet.Services.SalmaBenRomdhan;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tn.esprit.examen.Smartmeet.email.EmailService;
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
    private final EmailService emailService; // Ajout de EmailService

    public MeetingService(MeetingRepository meetingRepository,
                          AvailabilityService availabilityService,
                          UserRepository userRepository,
                          EmailService emailService) {
        this.meetingRepository = meetingRepository;
        this.availabilityService = availabilityService;
        this.userRepository = userRepository;
        this.emailService = emailService; // Injection de EmailService
    }

    public Meeting createInterview(MeetingRequest request) {
        // Récupérer l'utilisateur connecté comme organisateur
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Users organizer = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Organizer not found"));

        // Récupérer le participant seulement
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
        meeting.setOrganizer(organizer); // L'organisateur est toujours l'utilisateur connecté
        meeting.setParticipant(participant);

        // Sauvegarder la réunion
        Meeting savedMeeting = meetingRepository.save(meeting);

        // Envoyer l'e-mail d'invitation au participant
        String formattedStartTime = startTime.toString(); // Vous pouvez formater selon vos besoins
        emailService.sendMeetingInvitationEmail(
                participant.getEmail(), // Adresse e-mail du participant
                participant.getUsername(),
                meeting.getMeetingName(),
                organizer.getUsername(),
                participant.getUsername(),
                formattedStartTime,
                duration,
                meetingLink
        );

        return savedMeeting;
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

    public void deleteMeeting(Long id) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meeting not found with id: " + id));
        meetingRepository.delete(meeting);
    }
}
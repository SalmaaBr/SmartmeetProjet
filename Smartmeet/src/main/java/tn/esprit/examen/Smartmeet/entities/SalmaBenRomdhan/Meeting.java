package tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan;

import jakarta.persistence.*;
import tn.esprit.examen.Smartmeet.entities.Users.Users;

import java.time.LocalDateTime;

@Entity
public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String meetingName;
    private String meetingLink;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "organizer_id")
    private Users organizer;

    @ManyToOne
    @JoinColumn(name = "participant_id")
    private Users participant;

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMeetingName() { return meetingName; }
    public void setMeetingName(String meetingName) { this.meetingName = meetingName; }
    public String getMeetingLink() { return meetingLink; }
    public void setMeetingLink(String meetingLink) { this.meetingLink = meetingLink; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public Users getOrganizer() { return organizer; }
    public void setOrganizer(Users organizer) { this.organizer = organizer; }
    public Users getParticipant() { return participant; }
    public void setParticipant(Users participant) { this.participant = participant; }
}
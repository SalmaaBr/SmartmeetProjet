package tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan;


public class MeetingRequest {
    private String meetingName;
    private Long participantId; // ID du participant à interviewer
    private Integer durationMinutes; // Durée souhaitée (par défaut 5)

    // Getters et Setters
    public String getMeetingName() { return meetingName; }
    public void setMeetingName(String meetingName) { this.meetingName = meetingName; }
    public Long getParticipantId() { return participantId; }
    public void setParticipantId(Long participantId) { this.participantId = participantId; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
}

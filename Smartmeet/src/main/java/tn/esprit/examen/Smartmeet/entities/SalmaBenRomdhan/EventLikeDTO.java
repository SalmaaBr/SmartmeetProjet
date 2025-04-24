package tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan;


public class EventLikeDTO {
    private Long id;
    private Long userId;
    private Long eventId;
    private int likes;

    // Constructeurs
    public EventLikeDTO() {}

    public EventLikeDTO(Long id, Long userId, Long eventId, int likes) {
        this.id = id;
        this.userId = userId;
        this.eventId = eventId;
        this.likes = likes;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }
}

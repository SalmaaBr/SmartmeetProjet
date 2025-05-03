package tn.esprit.examen.Smartmeet.entities.GhanemRidene.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.LostAndFound;
import tn.esprit.examen.Smartmeet.entities.Users.Users;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Message implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, length = 1000)
    String content;

    @Column(nullable = false)
    LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    Users sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    Users receiver;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "chat_room_id", nullable = false)
    ChatRoom chatRoom;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "lost_found_id", nullable = false)
    LostAndFound lostAndFound;
    
    @Column(name = "is_read")
    boolean isRead = false;

    // Constructor without id
    public Message(String content, Users sender, Users receiver, ChatRoom chatRoom, LostAndFound lostAndFound) {
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
        this.chatRoom = chatRoom;
        this.lostAndFound = lostAndFound;
        this.timestamp = LocalDateTime.now();
    }
} 
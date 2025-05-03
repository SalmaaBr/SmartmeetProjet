package tn.esprit.examen.Smartmeet.entities.GhanemRidene.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
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
public class ChatParticipant implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "chat_room_id", nullable = false)
    ChatRoom chatRoom;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    Users user;
    
    @Column(nullable = false)
    LocalDateTime joinedAt;
    
    // Constructor
    public ChatParticipant(ChatRoom chatRoom, Users user) {
        this.chatRoom = chatRoom;
        this.user = user;
        this.joinedAt = LocalDateTime.now();
    }
} 
package tn.esprit.examen.Smartmeet.entities.GhanemRidene.chat;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.LostAndFound;
import tn.esprit.examen.Smartmeet.entities.Users.Users;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class ChatRoom implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "user1_id", nullable = false)
    Users user1;

    @ManyToOne
    @JoinColumn(name = "user2_id", nullable = false)
    Users user2;
    
    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    Users creator;

    @ManyToOne
    @JoinColumn(name = "lost_found_id", nullable = false)
    LostAndFound lostAndFound;

    @Column(nullable = false)
    LocalDateTime createdAt;
    
    @Column(name = "is_group_chat", nullable = false)
    boolean isGroupChat = false;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    List<Message> messages = new ArrayList<>();
    
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    List<ChatParticipant> participants = new ArrayList<>();
    
    // Constructor for one-to-one chat
    public ChatRoom(Users user1, Users user2, LostAndFound lostAndFound) {
        this.user1 = user1;
        this.user2 = user2;
        this.creator = user1;
        this.lostAndFound = lostAndFound;
        this.createdAt = LocalDateTime.now();
        this.isGroupChat = false;
    }
    
    // Constructor for group chat
    public ChatRoom(Users creator, LostAndFound lostAndFound, boolean isGroupChat) {
        this.creator = creator;
        this.user1 = creator; // Set both to creator for compatibility
        this.user2 = creator;
        this.lostAndFound = lostAndFound;
        this.createdAt = LocalDateTime.now();
        this.isGroupChat = isGroupChat;
    }
} 
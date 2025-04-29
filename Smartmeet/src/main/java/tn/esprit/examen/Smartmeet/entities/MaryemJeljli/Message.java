package tn.esprit.examen.Smartmeet.entities.MaryemJeljli;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import tn.esprit.examen.Smartmeet.entities.Users.Users;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "messages")
public class Message implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "sender_id", nullable = false)
    private Users sender;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "receiver_id", nullable = false)
    private Users receiver;

    @Column(nullable = false)
    private String content;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;
    @ManyToMany
    @JoinTable(
            name = "message_documents",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "document_id")
    )
    private List<Document> documents;
    // Constructors
    public Message() {
        this.sentAt = LocalDateTime.now();
        this.isRead = false;
    }

    // Getters and Setters
    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Users getSender() {
        return sender;
    }

    public void setSender(Users sender) {
        this.sender = sender;
    }

    public Users getReceiver() {
        return receiver;
    }

    public void setReceiver(Users receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
       this.isRead = read;
    }
}

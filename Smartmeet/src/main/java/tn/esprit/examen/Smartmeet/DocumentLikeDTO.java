package tn.esprit.examen.Smartmeet;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DocumentLikeDTO {
    private Long likeId;  // Changed from int to Long
    private String createdAt;  // Changed from LocalDateTime to String
    private Long documentId;
    private Long user_userid;

    public Long getLikeId() {
        return likeId;
    }

    public void setLikeId(Long likeId) {
        this.likeId = likeId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public Long getUser_userid() {
        return user_userid;
    }

    public void setUser_userid(Long user_userid) {
        this.user_userid = user_userid;
    }
}

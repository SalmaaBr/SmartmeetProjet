package tn.esprit.examen.Smartmeet;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Getter
@Setter

public class DocumentDTO {
    @NotBlank(message = "Name is required")
    private String name;
    private Integer id;

    private String description;

    private LocalDate createdAt;

    @NotBlank(message = "Document type is required")
    private String documentType;

    private String documentVisibility;

    private String documentAccessLevel;

    private String documentTheme;
    private List<DocumentLikeDTO> documentLikes;
    public List<DocumentLikeDTO> getDocumentLikes() {
        return documentLikes;
    }

    public void setDocumentLikes(List<DocumentLikeDTO> documentLikes) {
        this.documentLikes = documentLikes;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentVisibility() {
        return documentVisibility;
    }

    public void setDocumentVisibility(String documentVisibility) {
        this.documentVisibility = documentVisibility;
    }

    public String getDocumentAccessLevel() {
        return documentAccessLevel;
    }

    public void setDocumentAccessLevel(String documentAccessLevel) {
        this.documentAccessLevel = documentAccessLevel;
    }

    public String getDocumentTheme() {
        return documentTheme;
    }

    public void setDocumentTheme(String documentTheme) {
        this.documentTheme = documentTheme;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}

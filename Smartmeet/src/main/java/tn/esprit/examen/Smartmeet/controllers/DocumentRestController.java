package tn.esprit.examen.Smartmeet.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.Smartmeet.DocumentDTO;
import tn.esprit.examen.Smartmeet.DocumentLikeDTO;
import tn.esprit.examen.Smartmeet.Services.MaryemJeljli.IDocumentServices;
import tn.esprit.examen.Smartmeet.entities.MaryemJeljli.*;
import tn.esprit.examen.Smartmeet.entities.Users.Users;
import tn.esprit.examen.Smartmeet.repositories.Users.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
@RequestMapping("Document")
@RestController
@Tag(name="Document-Controller")

public class DocumentRestController {

    private final IDocumentServices documentServices;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(DocumentRestController.class);
    @GetMapping("/getAllDocumentLikes")
    public ResponseEntity<List<DocumentLikeDTO>> getAllDocumentLikes() {
        try {
            List<DocumentLikeDTO> likes = documentServices.getAllDocumentLikes();
            if (likes.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(likes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping("/AddDocument")
    public ResponseEntity<Document> addDocument(@Valid @RequestBody DocumentDTO documentDTO) {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        try {
            Document document = new Document();
            document.setName(documentDTO.getName());
            document.setDescription(documentDTO.getDescription());
            document.setCreatedAt(documentDTO.getCreatedAt());

            // Convertir les String en énumérations
            document.setDocumentType(TypeDocument.valueOf(documentDTO.getDocumentType()));
            if (documentDTO.getDocumentVisibility() != null) {
                document.setDocumentVisibility(TypeDocumentVisibility.valueOf(documentDTO.getDocumentVisibility()));
            }
            if (documentDTO.getDocumentAccessLevel() != null) {
                document.setDocumentAccessLevel(TypeAccessLevelDocument.valueOf(documentDTO.getDocumentAccessLevel()));
            }
            if (documentDTO.getDocumentTheme() != null) {
                document.setDocumentTheme(TypeDocumentTheme.valueOf(documentDTO.getDocumentTheme()));
            }

            Document createdDocument = documentServices.addDocument(document);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDocument);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (IllegalArgumentException e) {
            // Si une String ne correspond pas à une valeur d'énumération
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/ReadDocumentByID/{id}")
    public ResponseEntity<Document> retrieveDocument(@PathVariable Integer id) {
        try {
            Document document = documentServices.retrieveDocument(id);
            if (document == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(document);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/ReadAllDocuments")
    public ResponseEntity<List<DocumentDTO>> retrieveAllDocuments() {  // Changed return type to List<DocumentDTO>
        try {
            List<DocumentDTO> documents = documentServices.retrieveAllDocuments();
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/DeletDocumentByID/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Integer id) {
        try {
            documentServices.deleteDocument(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/UpdateDocumentByID/{id}")
    public ResponseEntity<Document> updateDocument(@PathVariable Integer id, @Valid @RequestBody DocumentDTO documentDTO) {
        try {
            Document document = new Document();
            document.setId(id);
            document.setName(documentDTO.getName());
            document.setDescription(documentDTO.getDescription());
            document.setCreatedAt(documentDTO.getCreatedAt());

            // Convertir les String en énumérations
            document.setDocumentType(TypeDocument.valueOf(documentDTO.getDocumentType()));
            if (documentDTO.getDocumentVisibility() != null) {
                document.setDocumentVisibility(TypeDocumentVisibility.valueOf(documentDTO.getDocumentVisibility()));
            }
            if (documentDTO.getDocumentAccessLevel() != null) {
                document.setDocumentAccessLevel(TypeAccessLevelDocument.valueOf(documentDTO.getDocumentAccessLevel()));
            }
            if (documentDTO.getDocumentTheme() != null) {
                document.setDocumentTheme(TypeDocumentTheme.valueOf(documentDTO.getDocumentTheme()));
            }

            documentServices.updateDocument(id, document);
            return ResponseEntity.ok(document);
        } catch (IllegalArgumentException e) {
            // Si une String ne correspond pas à une valeur d'énumération
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/like/{id}")
    public ResponseEntity<?> likeDocument(@PathVariable Integer id) {
        try {
            // Call the service method, which returns a Document
            Document document = documentServices.likeDocument(id);

            // Map Document to DocumentDTO manually
            DocumentDTO dto = new DocumentDTO();
            dto.setId(document.getId());
            dto.setName(document.getName());
            dto.setDescription(document.getDescription());
            if (document.getCreatedAt() != null) {
                dto.setCreatedAt(document.getCreatedAt());
            }
            dto.setDocumentType(document.getDocumentType() != null ? document.getDocumentType().name() : null);
            dto.setDocumentVisibility(document.getDocumentVisibility() != null ? document.getDocumentVisibility().name() : null);
            dto.setDocumentAccessLevel(document.getDocumentAccessLevel() != null ? document.getDocumentAccessLevel().name() : null);
            dto.setDocumentTheme(document.getDocumentTheme() != null ? document.getDocumentTheme().name() : null);
            List<DocumentLikeDTO> likeDtos = document.getDocumentLikes().stream().map(like -> {
                DocumentLikeDTO likeDto = new DocumentLikeDTO();
                likeDto.setLikeId(Long.valueOf(like.getLikeId()));
                likeDto.setCreatedAt(like.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME));
                likeDto.setDocumentId(Long.valueOf(like.getDocument().getId()));
                likeDto.setUser_userid(like.getUser().getUserID());
                return likeDto;
            }).collect(Collectors.toList());
            dto.setDocumentLikes(likeDtos);

            return ResponseEntity.ok(dto);
        } catch (IllegalStateException e) {
            // Handle case where user has already liked the document
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "User has already liked this document");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            // Handle unexpected errors
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "An error occurred while liking the document: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }}



    /*@PostMapping("/AddDocument")
    public Document addDocument(@RequestBody Document document) {

        return documentServices.addDocument(document);

    }

    @GetMapping("/ReadDocumentByID/{id}")
    public Document retrieveDocument(@PathVariable Integer id) {
        return documentServices.retrieveDocument(id);
    }

    @GetMapping("/ReadAllDocuments")
    public List<Document> retrieveAllDocuments() {
        return documentServices.retrieveAllDocuments();
    }

    @DeleteMapping("/DeletDocumentByID/{id}")
    public void deleteDocument(@PathVariable Integer id) {
        documentServices.deleteDocument(id);
    }

    @PutMapping("/UpdateDocumentByID/{id}")
    public void updateDocument(@PathVariable Integer id,@RequestBody Document document) {
        documentServices.updateDocument(id,document);
    }
    @PostMapping("/like/{id}")
    public ResponseEntity<Document> likeDocument(@PathVariable("id") Integer id) {
        try {
            Document updatedDocument = documentServices.likeDocument(id);
            return ResponseEntity.ok(updatedDocument);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null); // 409 Conflict si déjà liké
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    private Long getCurrentUserId() {
        // Exemple : Récupérer l'ID de l'utilisateur depuis le contexte de sécurité
        return 1L; // Remplacer par la logique réelle
    }


}*/

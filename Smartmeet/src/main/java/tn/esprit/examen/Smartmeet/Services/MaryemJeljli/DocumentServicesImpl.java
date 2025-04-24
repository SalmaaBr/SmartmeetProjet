package tn.esprit.examen.Smartmeet.Services.MaryemJeljli;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import tn.esprit.examen.Smartmeet.DocumentDTO;
import tn.esprit.examen.Smartmeet.DocumentLikeDTO;
import tn.esprit.examen.Smartmeet.entities.MaryemJeljli.Document;
import tn.esprit.examen.Smartmeet.entities.MaryemJeljli.DocumentLike;
import tn.esprit.examen.Smartmeet.entities.Users.Users;
import tn.esprit.examen.Smartmeet.repositories.MaryemJeljli.DocumentLikeRepository;
import tn.esprit.examen.Smartmeet.repositories.MaryemJeljli.IDocumentRepository;
import tn.esprit.examen.Smartmeet.repositories.Users.UserRepository;

import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;


    @RequiredArgsConstructor
    @Service

    public class DocumentServicesImpl implements IDocumentServices {
        private final UserRepository userRepository;
        private final IDocumentRepository documentRepository;
        private final DocumentLikeRepository documentLikeRepository;
        private static final Logger log = LoggerFactory.getLogger(DocumentServicesImpl.class);
        @Override
        public Document addDocument(Document document) {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = userDetails.getUsername();
            Users user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));

            document.setUsers(user);
            document.setDocumentLikes(new HashSet<>());
            return documentRepository.save(document);

        }

        @Override
        public List<DocumentDTO> retrieveAllDocuments() {
            List<Document> documents = documentRepository.findAll();
            return documents.stream().map(document -> {
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
                    likeDto.setLikeId(Long.valueOf(like.getLikeId()));  // Now matches Long type
                    likeDto.setCreatedAt(like.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME));  // Now matches String type
                    likeDto.setDocumentId(Long.valueOf(like.getDocument().getId()));
                    likeDto.setUser_userid(like.getUser().getUserID());
                    return likeDto;
                }).collect(Collectors.toList());
                dto.setDocumentLikes(likeDtos);
                return dto;
            }).collect(Collectors.toList());
        }



        @Override
        public Document retrieveDocument(Integer id) {
            Optional<Document> document = documentRepository.findById(id);
            return document.orElse(null);
        }

        @Override
        public void deleteDocument(Integer id) {
            documentRepository.deleteById(id);

        }

        @Override
        public void updateDocument(int id, Document document) {
            documentRepository.save(document);
        }

        @Override
        public Document likeDocument(Integer id) {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = userDetails.getUsername();
            Users user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));

            // Récupérer le document
            Document document = documentRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Document not found with id: " + id));

            // Vérifier si l'utilisateur a déjà liké le document
            if (documentLikeRepository.existsByDocumentAndUser(document, user)) {
                throw new IllegalStateException("User has already liked this document");
            }

            // Créer une nouvelle entrée dans DocumentLike
            DocumentLike documentLike = new DocumentLike();
            documentLike.setDocument(document);
            documentLike.setUser(user);
            documentLikeRepository.save(documentLike);

            // Mettre à jour la liste des likes dans le document
            document.getDocumentLikes().add(documentLike);
            return documentRepository.save(document);
        }

        @Override
        public List<DocumentLikeDTO> getAllDocumentLikes() {
            List<DocumentLike> likes = documentLikeRepository.findAll();
            return likes.stream().map(like -> {
                DocumentLikeDTO dto = new DocumentLikeDTO();
                dto.setLikeId(Long.valueOf(like.getLikeId()));  // Now matches Long type
                dto.setCreatedAt(like.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME));  // Now matches String type
                dto.setDocumentId(Long.valueOf(like.getDocument().getId()));
                dto.setUser_userid(like.getUser().getUserID());
                return dto;
            }).collect(Collectors.toList());
        }
    }







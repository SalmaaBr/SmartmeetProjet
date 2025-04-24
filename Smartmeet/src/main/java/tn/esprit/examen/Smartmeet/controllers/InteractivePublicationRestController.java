package tn.esprit.examen.Smartmeet.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.Smartmeet.Services.MaryemAbid.ContentModerationService;
import tn.esprit.examen.Smartmeet.Services.MaryemAbid.IInteractivePublicationServices;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.InteractivePublication;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.PublicationComment;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.PublicationLike;
import tn.esprit.examen.Smartmeet.entities.Users.Users;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@RequestMapping("InteractivePublication")
@RestController
@Tag(name="hello")
public class InteractivePublicationRestController {
    private final IInteractivePublicationServices interactivePublicationServices;
    private final ContentModerationService contentModerationService;

    @PostMapping("/create")
    public InteractivePublication createIPublication(@RequestBody InteractivePublication publication) {
        return interactivePublicationServices.createIPublication(publication);
    }

    @GetMapping("/ReadByID/{id}")
    public Optional<InteractivePublication> getIPublicationyID(@PathVariable int id) {
        return interactivePublicationServices.getIPublicationByID(id);
    }

    @GetMapping("/ReadAllIPublications")
    public List<InteractivePublication> getAllIPublications()  {
        return interactivePublicationServices.getAllIPublications();
    }

    @DeleteMapping("/DeleteIPublicationByID/{id}")
    public void deleteIPublication(@PathVariable int id){
        interactivePublicationServices.deleteIPublication(id);
    }

    @PutMapping("/UpdateIPublicationByID/{id}")
    public void updateIPublication(@PathVariable int id, @RequestBody InteractivePublication publication) {
        interactivePublicationServices.updateIPublication(id,publication);
    }

    // Comments related endpoints
    @GetMapping("/{publicationId}/comments")
   
    public List<PublicationComment> getCommentsByPublicationId(@PathVariable int publicationId) {
        return interactivePublicationServices.getCommentsByPublicationId(publicationId);
    }

    @PostMapping("/comments")
    public PublicationComment addComment(@RequestBody Map<String, Object> payload) {
        try {
            // Debug log the payload
            System.out.println("Received comment payload: " + payload);
            
            // Extract necessary data from the payload
            Integer publicationId = (Integer) payload.get("publicationId");
            Integer userId = (Integer) payload.get("userId");
            String content = (String) payload.get("content");
            
            if (publicationId == null || userId == null || content == null) {
                throw new RuntimeException("Missing required fields: publicationId, userId, or content");
            }
            
            // Create the comment with necessary relationships
            PublicationComment comment = new PublicationComment();
            comment.setContent(content);
            
            // Set up the publication reference
            InteractivePublication publication = new InteractivePublication();
            publication.setIpublicationId(publicationId);
            comment.setPublication(publication);
            
            // Set up the user reference
            Users user = new Users();
            user.setUserID(userId.longValue());
            comment.setUser(user);
            
            return interactivePublicationServices.addComment(comment);
        } catch (Exception e) {
            System.err.println("Error processing comment: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @PutMapping("/comments/{commentId}")
   
    public PublicationComment updateComment(@PathVariable int commentId, @RequestBody Map<String, String> payload) {
        String content = payload.get("content");
        return interactivePublicationServices.updateComment(commentId, content);
    }

    @DeleteMapping("/comments/{commentId}")
   
    public void deleteComment(@PathVariable int commentId) {
        interactivePublicationServices.deleteComment(commentId);
    }

    // Likes related endpoints
    @GetMapping("/{publicationId}/likes")
   
    public List<PublicationLike> getLikesByPublicationId(@PathVariable int publicationId) {
        return interactivePublicationServices.getLikesByPublicationId(publicationId);
    }

    @GetMapping("/{publicationId}/likes/count")
   
    public ResponseEntity<Integer> getLikesCount(@PathVariable int publicationId) {
        int count = interactivePublicationServices.getLikesCount(publicationId);
        return ResponseEntity.ok(count);
    }

    @PostMapping("/{publicationId}/likes/toggle")
   
    public ResponseEntity<Boolean> toggleLike(@PathVariable int publicationId, @RequestBody Map<String, Integer> payload) {
        int userId = payload.get("userId");
        boolean hasLiked = interactivePublicationServices.toggleLike(publicationId, userId);
        return ResponseEntity.ok(hasLiked);
    }

    @GetMapping("/{publicationId}/likes/user/{userId}")
   
    public ResponseEntity<Boolean> hasUserLiked(@PathVariable int publicationId, @PathVariable int userId) {
        boolean hasLiked = interactivePublicationServices.hasUserLiked(publicationId, userId);
        return ResponseEntity.ok(hasLiked);
    }

    @PostMapping("/moderation/check")
    public ResponseEntity<Map<String, Object>> checkContentModeration(@RequestBody Map<String, String> payload) {
        try {
            String content = payload.get("content");
            if (content == null || content.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Content is required"
                ));
            }

            boolean passesBadWords = contentModerationService.checkForBadWords(content);
            boolean passesIllegalContent = contentModerationService.checkForIllegalContent(content);
            boolean passesBadPublicity = contentModerationService.checkForBadPublicity(content);
            boolean passesAll = passesBadWords && passesIllegalContent && passesBadPublicity;

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("passes", passesAll);
            response.put("checks", Map.of(
                "badWords", passesBadWords,
                "illegalContent", passesIllegalContent,
                "badPublicity", passesBadPublicity
            ));

            if (!passesAll) {
                response.put("message", "Content failed moderation checks");
                
                if (!passesBadWords) {
                    response.put("badWordsMessage", "Content contains prohibited language");
                }
                
                if (!passesIllegalContent) {
                    response.put("illegalContentMessage", "Content may contain prohibited topics");
                }
                
                if (!passesBadPublicity) {
                    response.put("badPublicityMessage", "Content may contain negative references to brands");
                }
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Error checking content: " + e.getMessage()
            ));
        }
    }
}

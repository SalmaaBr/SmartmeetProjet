package tn.esprit.examen.Smartmeet.Services.MaryemAbid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.InteractivePublication;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.PublicationComment;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.PublicationLike;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.TypeIPublicationModerationStatus;
import tn.esprit.examen.Smartmeet.entities.Users.Users;
import tn.esprit.examen.Smartmeet.repositories.MaryemAbid.IInteractivePublicationRepository;
import tn.esprit.examen.Smartmeet.repositories.MaryemAbid.PublicationCommentRepository;
import tn.esprit.examen.Smartmeet.repositories.MaryemAbid.PublicationLikeRepository;
import tn.esprit.examen.Smartmeet.repositories.Users.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class InteractivePublicationServicesImpl implements IInteractivePublicationServices {
    private final IInteractivePublicationRepository interactivePublicationRepository;
    private final UserRepository usersRepository;
    private final PublicationCommentRepository commentRepository;
    private final PublicationLikeRepository likeRepository;
    private final ContentModerationService contentModerationService;

    @Override
    @Transactional
    public InteractivePublication createIPublication(InteractivePublication publication) {
        log.info("Creating new publication");
        
        // Check content for moderation issues
        if (publication.getTitle() != null || publication.getDescription() != null) {
            String contentToModerate = (publication.getTitle() != null ? publication.getTitle() : "") + " " + 
                                     (publication.getDescription() != null ? publication.getDescription() : "");
            
            boolean passesModeration = contentModerationService.moderateContent(contentToModerate);
            
            if (!passesModeration) {
                log.warn("Publication content failed moderation checks");
                // Set moderation status to FLAGGED
                publication.setPublicationModerationStatus(TypeIPublicationModerationStatus.FLAGGED);
            } else {
                // Content passed moderation
                publication.setPublicationModerationStatus(TypeIPublicationModerationStatus.APPROVED);
            }
        }
        
        // Set publication date if not set
        if (publication.getPublicationDate() == null) {
            publication.setPublicationDate(LocalDateTime.now());
        }
        
        return interactivePublicationRepository.save(publication);
    }

    @Override
    public Optional<InteractivePublication> getIPublicationByID(int id) {
        return interactivePublicationRepository.findById(id);
    }

    @Override
    public List<InteractivePublication> getAllIPublications() {
        return interactivePublicationRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteIPublication(int id) {
        log.info("Deleting publication with ID: {}", id);
        
        // Get the publication
        InteractivePublication publication = interactivePublicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publication not found with ID: " + id));
        
        // First delete dependent comments
        List<PublicationComment> comments = commentRepository.findByPublication(publication);
        if (!comments.isEmpty()) {
            log.info("Deleting {} comments for publication ID: {}", comments.size(), id);
            commentRepository.deleteAll(comments);
        }
        
        // Then delete dependent likes
        List<PublicationLike> likes = likeRepository.findByPublication(publication);
        if (!likes.isEmpty()) {
            log.info("Deleting {} likes for publication ID: {}", likes.size(), id);
            likeRepository.deleteAll(likes);
        }
        
        // Finally delete the publication
        interactivePublicationRepository.deleteById(id);
        log.info("Publication with ID: {} deleted successfully", id);
    }

    @Override
    @Transactional
    public void updateIPublication(int id, InteractivePublication publication) {
        log.info("Updating publication with ID: {}", id);
        
        // Fetch existing publication
        InteractivePublication existingPublication = interactivePublicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publication not found with ID: " + id));
        
        // Update fields
        existingPublication.setTitle(publication.getTitle());
        existingPublication.setDescription(publication.getDescription());
        existingPublication.setPublicationStatus(publication.getPublicationStatus());
        existingPublication.setPublicationVisibility(publication.getPublicationVisibility());
        existingPublication.setPublicationModerationStatus(publication.getPublicationModerationStatus());
        existingPublication.setScheduledPublishTime(publication.getScheduledPublishTime());
        
        // Save the updated entity
        interactivePublicationRepository.save(existingPublication);
        log.info("Publication with ID: {} updated successfully", id);
    }

    @Override
    public InteractivePublication addInteractivePublicationAndAssignToUser(InteractivePublication interactivePublication, Long userId) {
        Users user = usersRepository.findById(userId).orElse(null);
        if (user != null) {
            interactivePublication.setUser(user);
            return interactivePublicationRepository.save(interactivePublication);
        }
        return null;
    }
    
    // Comments related methods
    @Override
    public List<PublicationComment> getCommentsByPublicationId(int publicationId) {
        log.info("Fetching comments for publication with ID: {}", publicationId);
        InteractivePublication publication = interactivePublicationRepository.findById(publicationId)
                .orElseThrow(() -> new RuntimeException("Publication not found with ID: " + publicationId));
        return commentRepository.findByPublication(publication);
    }

    @Override
    @Transactional
    public PublicationComment addComment(PublicationComment comment) {
        log.info("Adding comment to publication");
        
        // First, check content moderation
        if (comment.getContent() != null && !comment.getContent().trim().isEmpty()) {
            boolean passesModeration = contentModerationService.moderateContent(comment.getContent());
            
            if (!passesModeration) {
                log.warn("Comment content failed moderation checks: {}", comment.getContent());
                throw new RuntimeException("Comment contains prohibited content and cannot be posted");
            }
        }
        
        // Extract the publication ID from the comment
        int publicationId;
        
        if (comment.getPublication() != null && comment.getPublication().getIpublicationId() > 0) {
            publicationId = comment.getPublication().getIpublicationId();
        } else {
            throw new RuntimeException("Publication ID is required");
        }
        
        log.info("Adding comment to publication with ID: {}", publicationId);
        
        // Fetch the actual entities from database
        InteractivePublication publication = interactivePublicationRepository.findById(publicationId)
                .orElseThrow(() -> new RuntimeException("Publication not found with ID: " + publicationId));
        
        Users user = null;
        if (comment.getUser() != null && comment.getUser().getUserID() != null) {
            user = usersRepository.findById(comment.getUser().getUserID())
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + comment.getUser().getUserID()));
        } else {
            throw new RuntimeException("User ID is required");
        }
        
        // Set the proper entities
        comment.setPublication(publication);
        comment.setUser(user);
        comment.setCreatedAt(LocalDateTime.now());
        
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public PublicationComment updateComment(int commentId, String content) {
        log.info("Updating comment with ID: {}", commentId);
        PublicationComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with ID: " + commentId));
        
        comment.setContent(content);
        comment.setUpdatedAt(LocalDateTime.now());
        
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void deleteComment(int commentId) {
        log.info("Deleting comment with ID: {}", commentId);
        commentRepository.deleteById(commentId);
    }
    
    // Likes related methods
    @Override
    public List<PublicationLike> getLikesByPublicationId(int publicationId) {
        log.info("Fetching likes for publication with ID: {}", publicationId);
        InteractivePublication publication = interactivePublicationRepository.findById(publicationId)
                .orElseThrow(() -> new RuntimeException("Publication not found with ID: " + publicationId));
        return likeRepository.findByPublication(publication);
    }

    @Override
    public int getLikesCount(int publicationId) {
        log.info("Counting likes for publication with ID: {}", publicationId);
        InteractivePublication publication = interactivePublicationRepository.findById(publicationId)
                .orElseThrow(() -> new RuntimeException("Publication not found with ID: " + publicationId));
        return likeRepository.countByPublication(publication);
    }

    @Override
    @Transactional
    public boolean toggleLike(int publicationId, int userId) {
        log.info("Toggling like for publication ID: {} by user ID: {}", publicationId, userId);
        InteractivePublication publication = interactivePublicationRepository.findById(publicationId)
                .orElseThrow(() -> new RuntimeException("Publication not found with ID: " + publicationId));
        
        Users user = usersRepository.findById((long) userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        // Check if user already liked the publication
        Optional<PublicationLike> existingLike = likeRepository.findByPublicationAndUser(publication, user);
        
        if (existingLike.isPresent()) {
            // User already liked, so remove the like
            likeRepository.delete(existingLike.get());
            return false;
        } else {
            // User hasn't liked, so add a new like
            PublicationLike newLike = new PublicationLike();
            newLike.setPublication(publication);
            newLike.setUser(user);
            newLike.setCreatedAt(LocalDateTime.now());
            likeRepository.save(newLike);
            return true;
        }
    }

    @Override
    public boolean hasUserLiked(int publicationId, int userId) {
        log.info("Checking if user ID: {} liked publication ID: {}", userId, publicationId);
        InteractivePublication publication = interactivePublicationRepository.findById(publicationId)
                .orElseThrow(() -> new RuntimeException("Publication not found with ID: " + publicationId));
        
        Users user = usersRepository.findById((long) userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        return likeRepository.findByPublicationAndUser(publication, user).isPresent();
    }
}

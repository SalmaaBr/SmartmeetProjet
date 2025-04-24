package tn.esprit.examen.Smartmeet.Services.MaryemAbid;

import tn.esprit.examen.Smartmeet.entities.MaryemAbid.InteractivePublication;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.PublicationComment;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.PublicationLike;

import java.util.List;
import java.util.Optional;

public interface IInteractivePublicationServices {
    InteractivePublication createIPublication(InteractivePublication publication);
    Optional<InteractivePublication> getIPublicationByID(int id);
    List<InteractivePublication> getAllIPublications();
    void deleteIPublication(int id);
    void updateIPublication(int id , InteractivePublication publication);
    InteractivePublication addInteractivePublicationAndAssignToUser(InteractivePublication interactivePublication, Long userId);
    
    // Comments related methods
    List<PublicationComment> getCommentsByPublicationId(int publicationId);
    PublicationComment addComment(PublicationComment comment);
    PublicationComment updateComment(int commentId, String content);
    void deleteComment(int commentId);
    
    // Likes related methods
    List<PublicationLike> getLikesByPublicationId(int publicationId);
    int getLikesCount(int publicationId);
    boolean toggleLike(int publicationId, int userId);
    boolean hasUserLiked(int publicationId, int userId);
}

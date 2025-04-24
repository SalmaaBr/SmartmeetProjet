package tn.esprit.examen.Smartmeet.entities.MaryemAbid;

public enum TypeIPublicationModerationStatus {
    PENDING,   // Publication is waiting for moderation
    APPROVED,  // Publication has passed moderation and is visible
    REJECTED,  // Publication has been rejected and is not visible
    FLAGGED    // Publication has been flagged for review by moderators
}

package tn.esprit.examen.Smartmeet.Services.MaryemAbid;

import tn.esprit.examen.Smartmeet.entities.MaryemAbid.InteractivePublication;

import java.util.List;
import java.util.Optional;

public interface IInteractivePublicationServices {
    InteractivePublication createIPublication(InteractivePublication publication);
    Optional<InteractivePublication> getIPublicationByID(int id);
    List<InteractivePublication> getAllIPublications();
    void deleteIPublication(int id);
    void updateIPublication(int id , InteractivePublication publication);
    InteractivePublication addInteractivePublicationAndAssignToUser(InteractivePublication interactivePublication, Long userId);
}

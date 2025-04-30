package tn.esprit.examen.Smartmeet.Services.MaryemSalhi;

import tn.esprit.examen.Smartmeet.entities.MaryemSalhi.MentalHealth;

import java.util.List;

public interface IMentalHealthServices {
    MentalHealth addMentalhealth(MentalHealth mentalhealth);
    MentalHealth updateMentalhealth(MentalHealth mentalhealth);
    void deleteMentalhealth(Long id);
    MentalHealth getMentalhealthById(Long id);
    List<MentalHealth> getAllMentalhealths();
    MentalHealth addMentalHealthForCurrentUser(MentalHealth mentalHealth);
    List<MentalHealth> getLastThreeSubmissionsByUser(Long userId); // Nouvelle méthode
}

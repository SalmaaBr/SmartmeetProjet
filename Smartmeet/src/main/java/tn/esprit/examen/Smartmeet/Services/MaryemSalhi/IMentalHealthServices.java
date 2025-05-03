package tn.esprit.examen.Smartmeet.Services.MaryemSalhi;

import tn.esprit.examen.Smartmeet.entities.MaryemSalhi.MentalHealth;

import java.util.List;

public interface IMentalHealthServices {
    MentalHealth addMentalhealth(MentalHealth mentalhealth);
    MentalHealth updateMentalhealth(MentalHealth mentalhealth);
    void deleteMentalhealth(Integer id);
    MentalHealth getMentalhealthById(int id);
    List<MentalHealth> getAllMentalhealths();
    MentalHealth addMentalHealthAndAssignToUser(MentalHealth mentalHealth, Long userId);

}

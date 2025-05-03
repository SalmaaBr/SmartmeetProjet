package tn.esprit.examen.Smartmeet.Services.MaryemSalhi;

import com.google.zxing.WriterException;
import tn.esprit.examen.Smartmeet.entities.MaryemSalhi.MentalHealth;

import java.io.IOException;
import java.util.List;

public interface IMentalHealthServices {
    MentalHealth addMentalhealth(MentalHealth mentalhealth);
    MentalHealth updateMentalhealth(MentalHealth mentalhealth);
    void deleteMentalhealth(Long id);
    MentalHealth getMentalhealthById(Long id);
    List<MentalHealth> getAllMentalhealths();

    List <MentalHealth> getAllMentalhealthsUsers();

    MentalHealth addMentalHealthForCurrentUser(MentalHealth mentalHealth);
    byte[] generateQRCode(Long mentalHealthId, int width, int height) throws WriterException, IOException;

    List<MentalHealth> getLastThreeSubmissionsByUser(Long userId); // Nouvelle méthode
}

package tn.esprit.examen.Smartmeet.Services.MaryemSalhi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tn.esprit.examen.Smartmeet.entities.MaryemSalhi.MentalHealth;
import tn.esprit.examen.Smartmeet.entities.Users.Users;
import tn.esprit.examen.Smartmeet.repositories.MaryemSalhi.IMentalhealthRepository;
import tn.esprit.examen.Smartmeet.repositories.Users.UserRepository;
import tn.esprit.examen.Smartmeet.security.services.UserDetailsImpl;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service

public class MentalHealthServicesImpl implements IMentalHealthServices {
    private final IMentalhealthRepository mentalhealthRepository;
    private final UserRepository usersRepository;
    private final UserRepository userRepository; // Déjà injecté pour addMentalHealthAndAssignToUser


    @Override
    public MentalHealth addMentalhealth(MentalHealth mentalhealth) {
        return mentalhealthRepository.save(mentalhealth);
    }

    @Override
    public MentalHealth updateMentalhealth(MentalHealth mentalhealth) {
        log.info("Updating mental health with ID: {}", mentalhealth.getIdMentalHealth());
        Optional<MentalHealth> existingMentalHealth = mentalhealthRepository.findById(mentalhealth.getIdMentalHealth());
        if (existingMentalHealth.isPresent()) {
            // Mettre à jour uniquement les champs modifiables, conserver l'utilisateur existant si non modifié
            MentalHealth current = existingMentalHealth.get();
            current.setResponseMoment(mentalhealth.getResponseMoment());
            current.setStressLevel(mentalhealth.getStressLevel());
            current.setEmotionalState(mentalhealth.getEmotionalState());
            current.setSupportNeed(mentalhealth.getSupportNeed());
            return mentalhealthRepository.save(current);
        } else {
            log.warn("MentalHealth with ID: {} not found", mentalhealth.getIdMentalHealth());
            throw new IllegalArgumentException("MentalHealth with ID " + mentalhealth.getIdMentalHealth() + " not found");
        }
    }

    @Override
    public void deleteMentalhealth(Long id) {
        mentalhealthRepository.deleteById(id);

    }


    @Override
    public MentalHealth getMentalhealthById(Long id) {
        return mentalhealthRepository.findById(id).orElse(null);

    }

    @Override
    public List<MentalHealth> getAllMentalhealths() {
        return mentalhealthRepository.findAll();
    }



    // Nouvelle méthode pour ajouter un formulaire avec l’utilisateur connecté
    public MentalHealth addMentalHealthForCurrentUser(MentalHealth mentalHealth) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Users user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        mentalHealth.setUser(user);
        MentalHealth savedMentalHealth = mentalhealthRepository.save(mentalHealth);

        // Générer une notification (simulée ici)
        String notification = user.getUsername() + " a soumis un formulaire de santé mentale.";
        System.out.println(notification); // Pour tester

        return savedMentalHealth;
    }
}



package tn.esprit.examen.Smartmeet.Services.MaryemSalhi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.examen.Smartmeet.entities.MaryemSalhi.MentalHealth;
import tn.esprit.examen.Smartmeet.entities.Users.Users;
import tn.esprit.examen.Smartmeet.repositories.MaryemSalhi.IMentalhealthRepository;
import tn.esprit.examen.Smartmeet.repositories.Users.UserRepository;

import java.util.List;
@Slf4j
@RequiredArgsConstructor
@Service

public class MentalHealthServicesImpl implements IMentalHealthServices {
    private final IMentalhealthRepository mentalhealthRepository;
    private final UserRepository usersRepository;


    @Override
    public MentalHealth addMentalhealth(MentalHealth mentalhealth) {
        return mentalhealthRepository.save(mentalhealth);
    }

    @Override
    public MentalHealth updateMentalhealth(MentalHealth mentalhealth) {
        return mentalhealthRepository.save(mentalhealth);
    }

    @Override
    public void deleteMentalhealth(Integer id) {
        mentalhealthRepository.deleteById(id);

    }


    @Override
    public MentalHealth getMentalhealthById(int id) {
        return mentalhealthRepository.findById(id).orElse(null);

    }

    @Override
    public List<MentalHealth> getAllMentalhealths() {
        return mentalhealthRepository.findAll();
    }

    @Override
    public MentalHealth addMentalHealthAndAssignToUser(MentalHealth mentalHealth, Long userId) {
        Users user = usersRepository.findById(userId).orElse(null);
        if (user != null) {
            mentalHealth.setUser(user);
            return mentalhealthRepository.save(mentalHealth);
        }
        return null;
    }

}


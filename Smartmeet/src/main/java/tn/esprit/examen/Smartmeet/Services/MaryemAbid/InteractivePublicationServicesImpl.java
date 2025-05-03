package tn.esprit.examen.Smartmeet.Services.MaryemAbid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.InteractivePublication;
import tn.esprit.examen.Smartmeet.entities.Users.Users;
import tn.esprit.examen.Smartmeet.repositories.MaryemAbid.IInteractivePublicationRepository;
import tn.esprit.examen.Smartmeet.repositories.Users.UserRepository;

import java.util.List;
import java.util.Optional;
@Slf4j
@RequiredArgsConstructor
@Service

public class InteractivePublicationServicesImpl implements IInteractivePublicationServices {
    private final IInteractivePublicationRepository interactivePublicationRepository;
    private final UserRepository usersRepository;

    @Override
    public InteractivePublication createIPublication(InteractivePublication publication) {
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
    public void deleteIPublication(int id) {
        interactivePublicationRepository.deleteById(id);

    }

    @Override
    public void updateIPublication(int id, InteractivePublication publication) {
        interactivePublicationRepository.save(publication);

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
}

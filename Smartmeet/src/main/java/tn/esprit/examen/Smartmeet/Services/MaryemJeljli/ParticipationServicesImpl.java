package tn.esprit.examen.Smartmeet.Services.MaryemJeljli;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.examen.Smartmeet.entities.MaryemJeljli.Participation;
import tn.esprit.examen.Smartmeet.repositories.MaryemJeljli.IParticipationRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service

public class ParticipationServicesImpl implements IParticipationServices {

    private final IParticipationRepository participationRepository;

    @Override
    public Participation addParticipation(Participation participation) {
        return participationRepository.save(participation);
    }

    @Override
    public Participation retrieveParticipation(int id) {
        return participationRepository.findById(id).orElse(null);
    }

    @Override
    public List<Participation> retrieveAllParticipations() {
        return participationRepository.findAll();
    }

    @Override
    public void deleteParticipation(int id) {
        participationRepository.deleteById(id);

    }

    @Override
    public void updateParticipation(int id,Participation participation) {
        participationRepository.save(participation);
    }

}

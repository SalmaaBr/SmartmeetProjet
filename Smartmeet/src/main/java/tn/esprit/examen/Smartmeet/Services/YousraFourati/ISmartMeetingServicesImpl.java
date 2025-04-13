package tn.esprit.examen.Smartmeet.Services.YousraFourati;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.examen.Smartmeet.entities.MaryemJeljli.Participation;
import tn.esprit.examen.Smartmeet.entities.YousraFourati.SmartMeeting;
import tn.esprit.examen.Smartmeet.repositories.MaryemJeljli.IParticipationRepository;
import tn.esprit.examen.Smartmeet.repositories.YousraFourati.ISmartMeetingRepository;

import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service

public class ISmartMeetingServicesImpl implements ISmartMeetingServices {
    private final ISmartMeetingRepository ismartMeetingRepository;
    private final IParticipationRepository participationRepository;


    @Override
    public List<SmartMeeting> getAllSmartMeetings() {
        return ismartMeetingRepository.findAll();
    }

    @Override
    public SmartMeeting getSmartMeetingById(int id) {
        return ismartMeetingRepository.findById(id).orElse(null);
    }

    @Override
    public SmartMeeting saveSmartMeeting(SmartMeeting smartMeeting) {
        return ismartMeetingRepository.save(smartMeeting);
    }

    @Override
    public void deleteSmartMeeting(int id) {
        ismartMeetingRepository.deleteById(id);
    }

    @Override
    public SmartMeeting assignParticipationToSmartMeeting(int smartMeetingId, Set<Participation> participations) {
        SmartMeeting smartMeeting = ismartMeetingRepository.findById(smartMeetingId).orElse(null);
        if (smartMeeting != null) {
            smartMeeting.setParticipation(participations);
            for (Participation participation : participations) {
                participation.setSmartMeeting(smartMeeting);
                participationRepository.save(participation);
            }
            return ismartMeetingRepository.save(smartMeeting);
        }
        return null;
    }



}

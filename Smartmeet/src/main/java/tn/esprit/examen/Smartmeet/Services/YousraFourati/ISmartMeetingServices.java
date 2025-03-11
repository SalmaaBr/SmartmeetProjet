package tn.esprit.examen.Smartmeet.Services.YousraFourati;

import tn.esprit.examen.Smartmeet.entities.MaryemJeljli.Participation;
import tn.esprit.examen.Smartmeet.entities.YousraFourati.SmartMeeting;

import java.util.List;
import java.util.Set;

public interface ISmartMeetingServices {
    List<SmartMeeting> getAllSmartMeetings();
    SmartMeeting getSmartMeetingById(int id);
    SmartMeeting saveSmartMeeting(SmartMeeting smartMeeting);
    void deleteSmartMeeting(int id);
    SmartMeeting assignParticipationToSmartMeeting(int smartMeetingId, Set<Participation> participations);
}

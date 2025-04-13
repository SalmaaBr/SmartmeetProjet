package tn.esprit.examen.Smartmeet.Services.MaryemJeljli;

import tn.esprit.examen.Smartmeet.entities.MaryemJeljli.Participation;

import java.util.List;

public interface IParticipationServices {
    Participation addParticipation(Participation participation);
    Participation retrieveParticipation(int id);
    List<Participation> retrieveAllParticipations();
    void deleteParticipation(int id);
    void updateParticipation(int id, Participation participation);
}

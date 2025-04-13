package tn.esprit.examen.Smartmeet.Services.YousraFourati;

import tn.esprit.examen.Smartmeet.entities.YousraFourati.Session;

import java.util.List;

public interface ISessionServices {
    List<Session> getAllSessions();
    Session getSessionById(int id);
    Session CreateSession(Session session);
    void deleteSession(int id);
    Session updateSession(int id, Session session);

}

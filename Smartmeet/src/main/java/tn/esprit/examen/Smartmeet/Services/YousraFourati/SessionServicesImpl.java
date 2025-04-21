package tn.esprit.examen.Smartmeet.Services.YousraFourati;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.examen.Smartmeet.entities.YousraFourati.Session;
import tn.esprit.examen.Smartmeet.repositories.YousraFourati.ISessionRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service

public class SessionServicesImpl implements ISessionServices {

    private final ISessionRepository iSessionRepository;

    @Override
    public List<Session> getAllSessions() {
        return iSessionRepository.findAll();    }

    @Override
    public Session getSessionById(int id) {
        return iSessionRepository.findById(id).orElse(null);    }

    @Override
    public Session CreateSession(Session session) {
        return iSessionRepository.save(session);
    }

    @Override
    public void deleteSession(int id) {
        iSessionRepository.deleteById(id);

    }

    @Override
    public Session updateSession(int id, Session session) {
        return iSessionRepository.save(session);
    }

}

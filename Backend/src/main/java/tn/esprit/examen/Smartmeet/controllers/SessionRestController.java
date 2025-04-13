package tn.esprit.examen.Smartmeet.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.Smartmeet.Services.YousraFourati.ISessionServices;
import tn.esprit.examen.Smartmeet.entities.YousraFourati.Session;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("Session")
@RestController
@Tag(name="hello")

public class SessionRestController {

    private final ISessionServices sessionServices;


    @GetMapping("/ReadAllSessions")
    public List<Session> getAllSessions() {
        return sessionServices.getAllSessions();
    }

    @GetMapping("/ReadSessionByID/{id}")
    public Session getSessionById(@PathVariable int id) {
        return sessionServices.getSessionById(id);
    }

    @PostMapping("/CreateSession")
    public Session createSession(@RequestBody Session session) {
        return sessionServices.CreateSession(session);
    }

    @PutMapping("/UpdateSession/{id}")
    public Session updateSession(@PathVariable int id, @RequestBody Session session) {
        return sessionServices.updateSession(id, session);
    }

    @DeleteMapping("/DeleteSession/{id}")
    public void deleteSession(@PathVariable int id,@RequestBody Session session) {
        sessionServices.deleteSession(id);
    }

}

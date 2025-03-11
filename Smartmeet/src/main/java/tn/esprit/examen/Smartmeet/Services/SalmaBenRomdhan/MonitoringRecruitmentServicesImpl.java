package tn.esprit.examen.Smartmeet.Services.SalmaBenRomdhan;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.MonitoringRecruitment;
import tn.esprit.examen.Smartmeet.entities.Users.Users;
import tn.esprit.examen.Smartmeet.repositories.SalmaBenRomdhan.IMonitoringRecruitmentRepository;
import tn.esprit.examen.Smartmeet.repositories.Users.UserRepository;

import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service

public class MonitoringRecruitmentServicesImpl implements IMonitoringRecruitmentServices {


    private final UserRepository userRepository;

    private final IMonitoringRecruitmentRepository monitoringRecruitmentRepository;

    @Override
    public MonitoringRecruitment createMonitoringRecruitment(MonitoringRecruitment monitoringRecruitment) {
        return monitoringRecruitmentRepository.save(monitoringRecruitment);
    }

    @Override
    public MonitoringRecruitment updateMonitoringRecruitment(Long id, MonitoringRecruitment monitoringRecruitment) {
        MonitoringRecruitment existingMonitoringRecruitment = monitoringRecruitmentRepository.findById(id).orElseThrow(() -> new RuntimeException("MonitoringRecruitment not found"));
        existingMonitoringRecruitment.setCreatedAt(monitoringRecruitment.getCreatedAt());
        existingMonitoringRecruitment.setStatus(monitoringRecruitment.getStatus());
        existingMonitoringRecruitment.setQuizId(monitoringRecruitment.getQuizId());
        existingMonitoringRecruitment.setQuizResultsLink(monitoringRecruitment.getQuizResultsLink());
        existingMonitoringRecruitment.setAlGeneratedReportLink(monitoringRecruitment.getAlGeneratedReportLink());
        existingMonitoringRecruitment.setCalendarLink(monitoringRecruitment.getCalendarLink());
        existingMonitoringRecruitment.setMeetingLink(monitoringRecruitment.getMeetingLink());
        existingMonitoringRecruitment.setResult(monitoringRecruitment.isResult());
        return monitoringRecruitmentRepository.save(existingMonitoringRecruitment);
    }

    @Override
    public void deleteMonitoringRecruitment(Long id) {
        monitoringRecruitmentRepository.deleteById(id);
    }

    @Override
    public MonitoringRecruitment getMonitoringRecruitmentById(Long id) {
        return monitoringRecruitmentRepository.findById(id).orElseThrow(() -> new RuntimeException("MonitoringRecruitment not found"));
    }

    @Override
    public List<MonitoringRecruitment> getAllMonitoringRecruitments() {
        return monitoringRecruitmentRepository.findAll();
    }

    @Override
    public void AddAndAssignMonitoringRecruitmentToUser(Long userID, MonitoringRecruitment monitoringRecruitment) {
        // Récupérer l'utilisateur par son ID
        Users user = userRepository.findById(userID)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Assigner l'utilisateur au suivi de recrutement
        monitoringRecruitment.setUsers(Collections.singleton(user));

        // Ajouter le suivi de recrutement à l'utilisateur
        user.getMonitoringrecruitments().add(monitoringRecruitment);

        // Sauvegarder le suivi de recrutement et l'utilisateur
        monitoringRecruitmentRepository.save(monitoringRecruitment);
        userRepository.save(user);
    }
}



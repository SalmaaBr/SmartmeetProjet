package tn.esprit.examen.Smartmeet.Services.SalmaBenRomdhan;

import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.MonitoringRecruitment;

import java.util.List;
import java.util.Set;

public interface IMonitoringRecruitmentServices {
    MonitoringRecruitment createMonitoringRecruitment(MonitoringRecruitment monitoringRecruitment);
    MonitoringRecruitment updateMonitoringRecruitment(Long id, MonitoringRecruitment monitoringRecruitment);
    void deleteMonitoringRecruitment(Long id);
    MonitoringRecruitment getMonitoringRecruitmentById(Long id);
    List<MonitoringRecruitment> getAllMonitoringRecruitments();
    void AddAndAssignMonitoringRecruitmentToUser(Long userID, MonitoringRecruitment monitoringRecruitment);
}

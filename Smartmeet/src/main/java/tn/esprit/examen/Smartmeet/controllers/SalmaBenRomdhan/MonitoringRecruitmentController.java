package tn.esprit.examen.Smartmeet.controllers.SalmaBenRomdhan;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.MonitoringRecruitment;
import tn.esprit.examen.Smartmeet.Services.SalmaBenRomdhan.IMonitoringRecruitmentServices;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("monitoringRecruitment")
@RestController
@CrossOrigin(origins = "*")
public class MonitoringRecruitmentController {
    @Autowired
    private IMonitoringRecruitmentServices monitoringRecruitmentService;

    @PostMapping
    public MonitoringRecruitment createMonitoringRecruitment(@RequestBody MonitoringRecruitment monitoringRecruitment) {
        return monitoringRecruitmentService.createMonitoringRecruitment(monitoringRecruitment);
    }

    @PutMapping("/{id}")
    public MonitoringRecruitment updateMonitoringRecruitment(@PathVariable Long id, @RequestBody MonitoringRecruitment monitoringRecruitment) {
        return monitoringRecruitmentService.updateMonitoringRecruitment(id, monitoringRecruitment);
    }

    @DeleteMapping("/{id}")
    public void deleteMonitoringRecruitment(@PathVariable Long id) {
        monitoringRecruitmentService.deleteMonitoringRecruitment(id);
    }

    @GetMapping("/{id}")
    public MonitoringRecruitment getMonitoringRecruitmentById(@PathVariable Long id) {
        return monitoringRecruitmentService.getMonitoringRecruitmentById(id);
    }

    @GetMapping
    public List<MonitoringRecruitment> getAllMonitoringRecruitments() {
        return monitoringRecruitmentService.getAllMonitoringRecruitments();
    }

    @PostMapping("/add/{userId}")
    public void addAndAssignMonitoringRecruitmentToUser(@PathVariable Long userId, @RequestBody MonitoringRecruitment monitoringRecruitment) {
        monitoringRecruitmentService.AddAndAssignMonitoringRecruitmentToUser(userId, monitoringRecruitment);
    }

}

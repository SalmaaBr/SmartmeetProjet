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
public class MonitoringRecruitmentController {
    @Autowired
    private IMonitoringRecruitmentServices monitoringRecruitmentService;

    @PostMapping("/createRecutement")
    public MonitoringRecruitment createMonitoringRecruitment(@RequestBody MonitoringRecruitment monitoringRecruitment) {
        return monitoringRecruitmentService.createMonitoringRecruitment(monitoringRecruitment);
    }

    @PutMapping("/updateRecutement/{id}")
    public MonitoringRecruitment updateMonitoringRecruitment(@PathVariable Long id, @RequestBody MonitoringRecruitment monitoringRecruitment) {
        return monitoringRecruitmentService.updateMonitoringRecruitment(id, monitoringRecruitment);
    }

    @DeleteMapping("/deleteRecutement/{id}")
    public void deleteMonitoringRecruitment(@PathVariable Long id) {
        monitoringRecruitmentService.deleteMonitoringRecruitment(id);
    }

    @GetMapping("/getrecutement/{id}")
    public MonitoringRecruitment getMonitoringRecruitmentById(@PathVariable Long id) {
        return monitoringRecruitmentService.getMonitoringRecruitmentById(id);
    }

    @GetMapping("/getrecutement")
    public List<MonitoringRecruitment> getAllMonitoringRecruitments() {
        return monitoringRecruitmentService.getAllMonitoringRecruitments();
    }

    @PostMapping("/add/{userId}")
    public void addAndAssignMonitoringRecruitmentToUser(@PathVariable Long userId, @RequestBody MonitoringRecruitment monitoringRecruitment) {
        monitoringRecruitmentService.AddAndAssignMonitoringRecruitmentToUser(userId, monitoringRecruitment);
    }

    @PostMapping("/assign-to-event/{title}")
    public void assignRecruitmentToEvent(@PathVariable("title") String title, @RequestBody MonitoringRecruitment monitoringRecruitment) {
        monitoringRecruitmentService.AddAndAssignRecruitmentToEvent(title, monitoringRecruitment);
    }

}

package tn.esprit.examen.Smartmeet.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.Smartmeet.Services.SalmaBenRomdhan.IMonitoringRecruitmentServices;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.MonitoringRecruitment;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("MonitoringRecruitment")
@RestController
@Tag(name="hello")

public class MonitoringRecruitmentRestController {

    private final IMonitoringRecruitmentServices monitoringRecruitmentServices;

    @PostMapping("/Create")
    public MonitoringRecruitment createMonitoringRecruitment(@RequestBody MonitoringRecruitment monitoringRecruitment) {
        return monitoringRecruitmentServices.createMonitoringRecruitment(monitoringRecruitment);
    }

    @PutMapping("/UpdateByID/{id}")
    public MonitoringRecruitment updateMonitoringRecruitment(@PathVariable Long id, @RequestBody MonitoringRecruitment monitoringRecruitment) {
        return monitoringRecruitmentServices.updateMonitoringRecruitment(id, monitoringRecruitment);
    }

    @DeleteMapping("/DeleteByID/{id}")
    public void deleteMonitoringRecruitment(@PathVariable Long id) {
        monitoringRecruitmentServices.deleteMonitoringRecruitment(id);
    }

    @GetMapping("/ReadByID/{id}")
    public MonitoringRecruitment getMonitoringRecruitmentById(@PathVariable Long id) {
        return monitoringRecruitmentServices.getMonitoringRecruitmentById(id);
    }

    @GetMapping("/ReadAll")
    public List<MonitoringRecruitment> getAllMonitoringRecruitments() {
        return monitoringRecruitmentServices.getAllMonitoringRecruitments();
    }

}

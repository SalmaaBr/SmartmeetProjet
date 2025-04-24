package tn.esprit.examen.Smartmeet.controllers.MaryemAbid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.Smartmeet.Services.MaryemAbid.IResourceServices;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.Resource;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.TypeResource;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.TypeResourceStatus;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestMapping("/api/resources")
@RestController
@RequiredArgsConstructor
@Slf4j
public class ResourceRestController {

    private final IResourceServices resourceServices;


    @GetMapping("/types")
    public ResponseEntity<List<String>> getResourceTypes() {
        return ResponseEntity.ok(Arrays.stream(TypeResource.values())
                .map(Enum::name)
                .collect(Collectors.toList()));
    }

    @GetMapping("/statuses")
    public ResponseEntity<List<String>> getResourceStatuses() {
        return ResponseEntity.ok(Arrays.stream(TypeResourceStatus.values())
                .map(Enum::name)
                .collect(Collectors.toList()));
    }



    @PostMapping("/create")
    public Resource createResource(@RequestBody Resource resource) {

        return resourceServices.createResource(resource);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> getResourceById(@PathVariable int id) {
        try {
            log.info("Getting resource with ID: {}", id);
            return resourceServices.getResourceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error getting resource {}: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/all")
    public List<Resource> getAllResources () {
        return resourceServices.getAllResources();
    }

    @DeleteMapping("/delete/{id}")
    public void deleteResource(@PathVariable int id){
        resourceServices. deleteResource(id);
    }

    @PutMapping("/update/{id}")
    public void updateResource(@PathVariable int id, @RequestBody Resource resource){
        resourceServices.updateResource(id,resource);
    }
    
    @PatchMapping("/{id}/maintenance")
    public ResponseEntity<Resource> configureMaintenancePeriod(
            @PathVariable int id,
            @RequestBody Map<String, Object> maintenanceConfig) {
        try {
            log.info("Configuring maintenance for resource ID: {}, config: {}", id, maintenanceConfig);
            
            // Get and convert parameters from the request
            Boolean enabled = Boolean.valueOf(String.valueOf(maintenanceConfig.get("maintenanceEnabled")));
            
            // Convert numbers safely
            Integer periodMonths = null;
            if (maintenanceConfig.get("maintenancePeriodMonths") != null) {
                try {
                    periodMonths = Integer.valueOf(String.valueOf(maintenanceConfig.get("maintenancePeriodMonths")));
                } catch (NumberFormatException e) {
                    log.warn("Error parsing maintenancePeriodMonths: {}", e.getMessage());
                }
            }
            
            Integer durationDays = null;
            if (maintenanceConfig.get("maintenanceDurationDays") != null) {
                try {
                    durationDays = Integer.valueOf(String.valueOf(maintenanceConfig.get("maintenanceDurationDays")));
                } catch (NumberFormatException e) {
                    log.warn("Error parsing maintenanceDurationDays: {}", e.getMessage());
                }
            }
            
            String initialDateStr = (String) maintenanceConfig.get("initialMaintenanceDate");
            LocalDate initialDate = initialDateStr != null ? LocalDate.parse(initialDateStr) : null;
            
            log.info("Parsed values - enabled: {}, periodMonths: {}, durationDays: {}, initialDate: {}", 
                    enabled, periodMonths, durationDays, initialDate);
            
            Resource updatedResource = resourceServices.updateMaintenanceSettings(
                id, enabled, periodMonths, durationDays, initialDate);
            
            log.info("Updated resource maintenance settings: {}", updatedResource);
            return ResponseEntity.ok(updatedResource);
        } catch (Exception e) {
            log.error("Error configuring maintenance for resource {}: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
}

package tn.esprit.examen.Smartmeet.Services.MaryemAbid;

import tn.esprit.examen.Smartmeet.entities.MaryemAbid.Resource;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IResourceServices {
    Resource createResource(Resource resource);
    Optional<Resource> getResourceById(int id);
    List<Resource> getAllResources();
    void deleteResource(int id);
    void updateResource(int id , Resource resource);
    
    // Maintenance-related methods
    Resource updateMaintenanceSettings(int id, Boolean enabled, Integer periodMonths, 
                                       Integer durationDays, LocalDate initialDate);
}

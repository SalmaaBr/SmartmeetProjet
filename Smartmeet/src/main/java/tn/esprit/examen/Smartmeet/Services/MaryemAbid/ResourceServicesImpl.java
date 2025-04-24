package tn.esprit.examen.Smartmeet.Services.MaryemAbid;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.Resource;
import tn.esprit.examen.Smartmeet.repositories.MaryemAbid.IResourceRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ResourceServicesImpl implements IResourceServices {

    private final IResourceRepository iresourceRepository;

    @Override
    public Resource createResource(Resource resource) {
        return iresourceRepository.save(resource);
    }

    @Override
    public Optional<Resource> getResourceById(int id) {
        return iresourceRepository.findById(id);
    }

    @Override
    public List<Resource> getAllResources() {
        return iresourceRepository.findAll();
    }

    @Override
    public void deleteResource(int id) {
        iresourceRepository.deleteById(id);
    }

    @Override
    public void updateResource(int id, Resource resource) {
        iresourceRepository.save(resource);
    }
    
    @Override
    @Transactional
    public Resource updateMaintenanceSettings(int id, Boolean enabled, Integer periodMonths, 
                                             Integer durationDays, LocalDate initialDate) {
        log.info("Updating maintenance settings for resource ID: {}", id);
        log.info("Parameters - enabled: {}, periodMonths: {}, durationDays: {}, initialDate: {}", 
                enabled, periodMonths, durationDays, initialDate);
        
        Resource resource = iresourceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found with ID: " + id));
        
        log.info("Found resource: {}", resource);
        
        if (enabled != null) {
            resource.setMaintenanceEnabled(enabled);
            log.info("Setting maintenance enabled: {}", enabled);
        }
        
        if (periodMonths != null && periodMonths > 0) {
            resource.setMaintenancePeriodMonths(periodMonths);
            log.info("Setting maintenance period months: {}", periodMonths);
        }
        
        if (durationDays != null && durationDays > 0) {
            resource.setMaintenanceDurationDays(durationDays);
            log.info("Setting maintenance duration days: {}", durationDays);
        }
        
        if (initialDate != null) {
            resource.setInitialMaintenanceDate(initialDate);
            log.info("Setting initial maintenance date: {}", initialDate);
            
            // This will trigger the @PreUpdate to calculate the next maintenance date
        }
        
        // Save the updated resource
        Resource savedResource = iresourceRepository.save(resource);
        log.info("Saved resource with updated maintenance settings: {}", savedResource);
        
        return savedResource;
    }
}


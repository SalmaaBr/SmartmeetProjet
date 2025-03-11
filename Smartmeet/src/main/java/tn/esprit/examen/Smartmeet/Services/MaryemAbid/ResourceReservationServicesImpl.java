package tn.esprit.examen.Smartmeet.Services.MaryemAbid;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.Resource;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.ResourceReservation;
import tn.esprit.examen.Smartmeet.repositories.MaryemAbid.IResourceRepository;
import tn.esprit.examen.Smartmeet.repositories.MaryemAbid.IResourceReservationRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service

public class ResourceReservationServicesImpl implements IResourceReservationServices {

    private final IResourceReservationRepository reservationRepository;
    private final IResourceRepository resourceRepository;

    @Override
    public ResourceReservation createResourceReservation(ResourceReservation resourceReservation) {
        return reservationRepository.save(resourceReservation);
    }

    @Override
    public Optional<ResourceReservation> getResourceReservationByID(int id) {
        return reservationRepository.findById((long) id);
    }

    @Override
    public List<ResourceReservation> getAllResourceReservations() {
        return reservationRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteResourceReservation(Long id) {
        ResourceReservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Resource reservation not found with ID: " + id));

        // Break relationships with resources first
        for (Resource resource : reservation.getResources()) {
            resource.setResourceReservation(null); // Remove foreign key reference
            resourceRepository.save(resource); // Update resource to nullify FK
        }

        reservationRepository.delete(reservation); // Now safe to delete
    }
    @Override
    @Transactional
    public void updateResourceReservation(Long id, ResourceReservation resourceReservation) {
        ResourceReservation existingReservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Resource reservation not found with ID: " + id));

        // Update basic fields
        existingReservation.setStartTime(resourceReservation.getStartTime());
        existingReservation.setEndTime(resourceReservation.getEndTime());
        existingReservation.setUser(resourceReservation.getUser());

        // Update resources
        if (resourceReservation.getResources() != null) {
            // Clear existing resources by breaking the relationship
            existingReservation.getResources().forEach(res -> res.setResourceReservation(null));
            existingReservation.getResources().clear();

            // Assign new resources
            for (Resource newResource : resourceReservation.getResources()) {
                Resource managedResource = resourceRepository.findById(newResource.getIdResource())
                        .orElseThrow(() -> new EntityNotFoundException("Resource not found with ID: " + newResource.getIdResource()));
                managedResource.setResourceReservation(existingReservation);
                existingReservation.getResources().add(managedResource);
            }
        }

        reservationRepository.save(existingReservation);
    }

    @Override
    @Transactional
    public ResourceReservation addResourceReservationAndAssignToResource(ResourceReservation resourceReservation, Integer resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found with ID: " + resourceId));

        // Ensure the resources list is initialized
        if (resourceReservation.getResources() == null) {
            resourceReservation.setResources(new ArrayList<>());
        }

        // Save the new reservation first to generate an ID
        ResourceReservation savedReservation = reservationRepository.save(resourceReservation);

        // Assign the reservation to the resource
        resource.setResourceReservation(savedReservation);
        savedReservation.getResources().add(resource);

        // Save the resource to update the relationship
        resourceRepository.save(resource);

        return savedReservation;
    }



}

package tn.esprit.examen.Smartmeet.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.Smartmeet.Services.MaryemAbid.IResourceReservationServices;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.ResourceReservation;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.TypeResource;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.TypeResourceStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("/api/reservations")
@RestController
@Tag(name="hello")
public class ResourceReservationRestController {
    private final IResourceReservationServices resourceReservationServices;


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

    @PostMapping("/create/{resourceId}")
    public ResourceReservation createResourceReservation(@RequestBody ResourceReservation resourceReservation, @PathVariable int resourceId) {

        return resourceReservationServices.addResourceReservationAndAssignToResource(resourceReservation, resourceId);
    }

    @GetMapping("/{id}")
    public Optional<ResourceReservation> getResourceReservationByID(@PathVariable int id){
        return resourceReservationServices.getResourceReservationByID(id);
    }


    @GetMapping("/all")
    public List<ResourceReservation> getAllResourceReservations() {
        return resourceReservationServices.getAllResourceReservations();
    }

    @DeleteMapping("/delete/{id}")
    public void deleteResourceReservation(@PathVariable Long id){
        resourceReservationServices.deleteResourceReservation(id);
    }

    @PutMapping("/update/{id}")
    public void updateResourceReservation(@PathVariable Long id, @RequestBody ResourceReservation resourceReservation) {
        resourceReservationServices.updateResourceReservation(id,resourceReservation);
    }


}


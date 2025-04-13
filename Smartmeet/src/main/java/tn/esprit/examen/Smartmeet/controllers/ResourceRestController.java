package tn.esprit.examen.Smartmeet.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.Smartmeet.Services.MaryemAbid.IResourceServices;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.Resource;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.TypeResource;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.TypeResourceStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("/api/resources")
@RestController
@Tag(name="hello")

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
    public Optional<Resource> getResourceByID(@PathVariable int id) {
        return resourceServices.getResourceByID(id);
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
}

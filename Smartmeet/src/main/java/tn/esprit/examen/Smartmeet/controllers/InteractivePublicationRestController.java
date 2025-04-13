package tn.esprit.examen.Smartmeet.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.Smartmeet.Services.MaryemAbid.IInteractivePublicationServices;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.InteractivePublication;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RequestMapping("InteractivePublication")
@RestController
@Tag(name="hello")

public class InteractivePublicationRestController {
    private final IInteractivePublicationServices interactivePublicationServices;

    @PostMapping("/create")
    public InteractivePublication createIPublication(@RequestBody InteractivePublication publication) {

        return interactivePublicationServices.createIPublication(publication);
    }

    @GetMapping("/ReadByID/{id}")
    public Optional<InteractivePublication> getIPublicationyID(@PathVariable int id) {
        return interactivePublicationServices.getIPublicationByID(id);
    }


    @GetMapping("/ReadAllIPublications")
    public List<InteractivePublication> getAllIPublications()  {
        return interactivePublicationServices.getAllIPublications();
    }

    @DeleteMapping("/DeleteIPublicationByID/{id}")
    public void deleteIPublication(@PathVariable int id){
        interactivePublicationServices.deleteIPublication(id);
    }

    @PutMapping("/UpdateIPublicationByID/id")
    public void updateIPublication(@PathVariable int id, @RequestBody InteractivePublication publication) {
        interactivePublicationServices.updateIPublication(id,publication);
    }


}

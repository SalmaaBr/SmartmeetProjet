package tn.esprit.examen.Smartmeet.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.Smartmeet.Services.YousraFourati.ITagsServices;
import tn.esprit.examen.Smartmeet.entities.YousraFourati.Tags;

import java.util.List;
@RequiredArgsConstructor
@RequestMapping("Resource")
@RestController
@Tag(name="hello")


public class TagsRestController {
    private final ITagsServices tagsServices;



    @GetMapping
    public List<Tags> getAllTags() {
        return tagsServices.getAllTags();
    }

    @PostMapping
    public Tags addTag(@RequestBody Tags tag) {
        return tagsServices.addTag(tag);
    }
}

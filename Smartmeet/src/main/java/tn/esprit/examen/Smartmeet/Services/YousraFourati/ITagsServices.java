package tn.esprit.examen.Smartmeet.Services.YousraFourati;

import tn.esprit.examen.Smartmeet.entities.YousraFourati.Tags;

import java.util.List;

public interface ITagsServices {
    List<Tags> getAllTags();
    Tags addTag(Tags tag);

}

package tn.esprit.examen.Smartmeet.Services.YousraFourati;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.examen.Smartmeet.entities.YousraFourati.Tags;
import tn.esprit.examen.Smartmeet.repositories.YousraFourati.ITagsRepository;

import java.util.List;
@Slf4j
@RequiredArgsConstructor
@Service

public class TagsServicesImpl implements ITagsServices {
    private final ITagsRepository itagsRepository;

    @Override
    public List<Tags> getAllTags() {
        return itagsRepository.findAll();
    }

    @Override
    public Tags addTag(Tags tag) {
        return itagsRepository.save(tag);
    }
}

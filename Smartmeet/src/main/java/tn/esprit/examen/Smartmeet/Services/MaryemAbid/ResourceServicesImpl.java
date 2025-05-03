package tn.esprit.examen.Smartmeet.Services.MaryemAbid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.Resource;
import tn.esprit.examen.Smartmeet.repositories.MaryemAbid.IResourceRepository;

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
    public Optional<Resource> getResourceByID(int id) {
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
}


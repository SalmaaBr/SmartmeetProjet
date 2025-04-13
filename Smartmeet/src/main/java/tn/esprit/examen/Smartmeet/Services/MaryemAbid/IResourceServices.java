package tn.esprit.examen.Smartmeet.Services.MaryemAbid;

import tn.esprit.examen.Smartmeet.entities.MaryemAbid.Resource;

import java.util.List;
import java.util.Optional;

public interface IResourceServices {
    Resource createResource(Resource resource);
    Optional<Resource> getResourceByID(int id);
    List<Resource> getAllResources();
    void deleteResource(int id);
    void updateResource(int id , Resource resource);
}

package tn.esprit.examen.Smartmeet.Services.MaryemJeljli;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.examen.Smartmeet.DocumentDTO;
import tn.esprit.examen.Smartmeet.DocumentLikeDTO;
import tn.esprit.examen.Smartmeet.entities.MaryemJeljli.Document;
import tn.esprit.examen.Smartmeet.entities.Users.Users;

import java.util.List;

public interface IDocumentServices {
    Document addDocument(Document document);
    List<DocumentDTO> retrieveAllDocuments();

    Document retrieveDocument(Integer id);
    void deleteDocument(Integer id);
    void updateDocument(int id , Document document);
    Document likeDocument(Integer id);
    List<DocumentLikeDTO> getAllDocumentLikes();
}

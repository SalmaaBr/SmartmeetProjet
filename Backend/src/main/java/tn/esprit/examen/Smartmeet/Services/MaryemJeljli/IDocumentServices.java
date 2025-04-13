package tn.esprit.examen.Smartmeet.Services.MaryemJeljli;

import tn.esprit.examen.Smartmeet.entities.MaryemJeljli.Document;

import java.util.List;

public interface IDocumentServices {
    Document addDocument(Document document);
    List<Document> retrieveAllDocuments();
    Document retrieveDocument(Integer id);
    void deleteDocument(Integer id);
    void updateDocument(int id , Document document);

}

package tn.esprit.examen.Smartmeet.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.Smartmeet.Services.MaryemJeljli.IDocumentServices;
import tn.esprit.examen.Smartmeet.entities.MaryemJeljli.Document;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("Document")
@RestController
@Tag(name="hello")

public class DocumentRestController {

    private final IDocumentServices documentServices;

    @PostMapping("/AddDocument")
    public Document addDocument(@RequestBody Document document) {
        return documentServices.addDocument(document);
    }

    @GetMapping("/ReadDocumentByID/{id}")
    public Document retrieveDocument(@PathVariable Integer id) {
        return documentServices.retrieveDocument(id);
    }

    @GetMapping("/ReadAllDocuments")
    public List<Document> retrieveAllDocuments() {
        return documentServices.retrieveAllDocuments();
    }

    @DeleteMapping("/DeletDocumentByID/{id}")
    public void deleteDocument(@PathVariable Integer id) {
        documentServices.deleteDocument(id);
    }

    @PutMapping("/UpdateDocumentByID/{id}")
    public void updateDocument(@PathVariable Integer id,@RequestBody Document document) {
        documentServices.updateDocument(id,document);
    }


}

package tn.esprit.examen.Smartmeet.Services.MaryemJeljli;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.examen.Smartmeet.entities.MaryemJeljli.Document;
import tn.esprit.examen.Smartmeet.repositories.MaryemJeljli.IDocumentRepository;

import java.util.List;
import java.util.Optional;


    @Slf4j
    @RequiredArgsConstructor
    @Service

    public class DocumentServicesImpl implements IDocumentServices {

        private final IDocumentRepository documentRepository;

        @Override
        public Document addDocument(Document document) {
            return documentRepository.save(document);
        }

        @Override
        public List<Document> retrieveAllDocuments() {
            return documentRepository.findAll();
        }



        @Override
        public Document retrieveDocument(Integer id) {
            Optional<Document> document = documentRepository.findById(id);
            return document.orElse(null);
        }

        @Override
        public void deleteDocument(Integer id) {
            documentRepository.deleteById(id);

        }

        @Override
        public void updateDocument(int id, Document document) {
            documentRepository.save(document);
        }


    }



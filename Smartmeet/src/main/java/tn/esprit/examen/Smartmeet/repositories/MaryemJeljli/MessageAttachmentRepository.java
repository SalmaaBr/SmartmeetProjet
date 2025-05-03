package tn.esprit.examen.Smartmeet.repositories.MaryemJeljli;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.examen.Smartmeet.entities.MaryemJeljli.Message;
import tn.esprit.examen.Smartmeet.entities.MaryemJeljli.MessageAttachment;

import java.util.List;

public interface MessageAttachmentRepository extends JpaRepository<MessageAttachment, Long> {
    List<MessageAttachment> findByMessage(Message message);
}

package tn.esprit.examen.Smartmeet.repositories.MaryemJeljli;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.examen.Smartmeet.entities.MaryemJeljli.Message;
import tn.esprit.examen.Smartmeet.entities.Users.Users;


import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByReceiverOrderBySentAtDesc(Users receiver);
    List<Message> findBySenderOrderBySentAtDesc(Users sender);
    List<Message> findByReceiver(Users receiver);

    // Find messages by sender
    List<Message> findBySender(Users sender);

    // Count unread messages for a receiver
    long countByReceiverAndIsReadFalse(Users receiver);
}
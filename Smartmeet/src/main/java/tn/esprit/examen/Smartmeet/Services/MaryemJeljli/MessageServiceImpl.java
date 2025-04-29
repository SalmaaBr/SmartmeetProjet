package tn.esprit.examen.Smartmeet.Services.MaryemJeljli;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import tn.esprit.examen.Smartmeet.entities.MaryemJeljli.Document;
import tn.esprit.examen.Smartmeet.entities.MaryemJeljli.Message;
import tn.esprit.examen.Smartmeet.entities.MaryemJeljli.MessageAttachment;
import tn.esprit.examen.Smartmeet.entities.Users.Users;
import tn.esprit.examen.Smartmeet.repositories.MaryemJeljli.IDocumentRepository;
import tn.esprit.examen.Smartmeet.repositories.MaryemJeljli.MessageAttachmentRepository;
import tn.esprit.examen.Smartmeet.repositories.MaryemJeljli.MessageRepository;
import tn.esprit.examen.Smartmeet.repositories.Users.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MessageServiceImpl implements IMessageService {

    @Autowired
    private  MessageRepository messageRepository;

    @Autowired
    private MessageAttachmentRepository messageAttachmentRepository;

    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private IDocumentRepository documentRepository;


    @Override
    public Message sendMessage(String receiverUsername, String content, List<Integer> documentIds) {
        String senderUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Users sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        Users receiver = userRepository.findByUsername(receiverUsername)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setSentAt(LocalDateTime.now());
        message.setRead(false);

        if (documentIds != null && !documentIds.isEmpty()) {
            List<Document> documents = new ArrayList<>();
            for (Integer id : documentIds) {
                Document document = documentRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Document not found with ID: " + id));
                documents.add(document);
            }
            message.setDocuments(documents);
        }

        return messageRepository.save(message);
    }

    @Override
    @Transactional
    public List<Message> getInbox() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Message> messages = messageRepository.findByReceiver(user);
        messages.forEach(message -> {
            if (message.getDocuments() != null) {
                message.getDocuments().size();
            }
        });
        return messages;
    }

    @Override
    @Transactional
    public List<Message> getSentMessages() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Message> messages = messageRepository.findBySender(user);
        messages.forEach(message -> {
            if (message.getDocuments() != null) {
                message.getDocuments().size();
            }
        });
        return messages;
    }

    @Override
    public void markAsRead(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        message.setRead(true);
        messageRepository.save(message);
    }

    @Override
    public long getUnreadMessageCount() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return messageRepository.countByReceiverAndIsReadFalse(user);
    }
}

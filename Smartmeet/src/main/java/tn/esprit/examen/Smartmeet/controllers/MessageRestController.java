package tn.esprit.examen.Smartmeet.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.Smartmeet.Services.MaryemJeljli.IMessageService;
import tn.esprit.examen.Smartmeet.entities.MaryemJeljli.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/messages")
public class MessageRestController {

    @Autowired
    private IMessageService messageService;

    @PostMapping("/send")

    public ResponseEntity<?> sendMessage(@RequestBody Map<String, Object> request) {
        try {
            String receiverUsername = (String) request.get("receiverUsername");
            String content = (String) request.get("content");
            List<Integer> documentIds = (List<Integer>) request.get("documentIds");

            Message message = messageService.sendMessage(receiverUsername, content, documentIds);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error sending message: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/inbox")
    public ResponseEntity<List<Message>> getInbox() {
        try {
            List<Message> inbox = messageService.getInbox();
            return ResponseEntity.ok(inbox);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/sent")
    public ResponseEntity<List<Message>> getSentMessages() {
        try {
            List<Message> sentMessages = messageService.getSentMessages();
            return ResponseEntity.ok(sentMessages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        try {
            messageService.markAsRead(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error marking message as read: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadMessageCount() {
        try {
            long count = messageService.getUnreadMessageCount();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

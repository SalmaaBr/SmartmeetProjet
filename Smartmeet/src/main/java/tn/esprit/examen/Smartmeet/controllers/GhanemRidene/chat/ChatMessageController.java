package tn.esprit.examen.Smartmeet.controllers.GhanemRidene.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import tn.esprit.examen.Smartmeet.Services.GhanemRidene.chat.ChatService;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.chat.Message;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/chat.sendMessage/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, @Payload Map<String, String> payload) {
        try {
            // Extract message details from payload
            String content = payload.get("content");
            Long senderId = Long.parseLong(payload.get("senderId"));
            
            // Save message to database
            Message message = chatService.sendMessage(senderId, roomId, content);
            
            // Convert to a simplified map for transfer
            Map<String, Object> messageData = new HashMap<>();
            messageData.put("id", message.getId());
            messageData.put("content", message.getContent());
            messageData.put("senderId", message.getSender().getUserID());
            messageData.put("senderUsername", message.getSender().getUsername());
            messageData.put("timestamp", message.getTimestamp().toString());
            messageData.put("read", message.isRead());

            // Get the receiver's user ID for notification
            Long receiverId = message.getReceiver().getUserID();
            
            // Broadcast to the chat room topic
            messagingTemplate.convertAndSend("/topic/chat." + roomId, messageData);
            
            // Also send to a private user queue for notifications
            messagingTemplate.convertAndSend("/queue/user." + receiverId, messageData);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to send message: " + e.getMessage());
            messagingTemplate.convertAndSend("/topic/errors", errorResponse);
        }
    }

    @MessageMapping("/chat.typing/{roomId}")
    public void typingNotification(@DestinationVariable Long roomId, @Payload Map<String, Object> payload) {
        // Extract user info
        Long userId = Long.valueOf(payload.get("userId").toString());
        String username = payload.get("username").toString();
        boolean typing = Boolean.valueOf(payload.get("typing").toString());
        
        // Create typing status message
        Map<String, Object> typingStatus = new HashMap<>();
        typingStatus.put("userId", userId);
        typingStatus.put("username", username);
        typingStatus.put("typing", typing);
        
        // Send to everyone in the room
        messagingTemplate.convertAndSend("/topic/chat." + roomId + ".typing", typingStatus);
    }
} 
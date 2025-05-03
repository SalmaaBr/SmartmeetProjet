package tn.esprit.examen.Smartmeet.controllers.GhanemRidene.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.Smartmeet.Services.GhanemRidene.chat.ChatService;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.chat.ChatParticipant;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.chat.ChatRoom;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.chat.Message;
import tn.esprit.examen.Smartmeet.repositories.Users.UserRepository;
import tn.esprit.examen.Smartmeet.security.services.UserDetailsImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class ChatController {

    private final ChatService chatService;
    private final UserRepository userRepository;

    @PostMapping("/room")
    public ResponseEntity<Map<String, Object>> createOrGetChatRoom(
            @RequestParam Long itemCreatorId,
            @RequestParam Long lostFoundId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        try {
            Long currentUserId = Long.parseLong(userDetails.getId());
            ChatRoom chatRoom = chatService.findOrCreateChatRoom(currentUserId, itemCreatorId, lostFoundId);
            
            // Create a simplified response map without circular references
            return buildChatRoomResponse(chatRoom, lostFoundId);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create chat room: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/room")
    public ResponseEntity<Map<String, Object>> getChatRoomByParams(
            @RequestParam(required = false) Long itemCreatorId,
            @RequestParam Long lostFoundId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        try {
            Long currentUserId = Long.parseLong(userDetails.getId());
            ChatRoom chatRoom;
            
            // If itemCreatorId is provided, use one-to-one chat, otherwise use group chat
            if (itemCreatorId != null) {
                chatRoom = chatService.findOrCreateChatRoom(currentUserId, itemCreatorId, lostFoundId);
            } else {
                chatRoom = chatService.findOrCreateGroupChatRoom(lostFoundId, currentUserId);
            }
            
            // Create a response with participants if it's a group chat
            if (chatRoom.isGroupChat()) {
                return buildGroupChatRoomResponse(chatRoom, lostFoundId);
            } else {
                return buildChatRoomResponse(chatRoom, lostFoundId);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to get chat room: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    // Helper method to build a chat room response without circular references
    private ResponseEntity<Map<String, Object>> buildChatRoomResponse(ChatRoom chatRoom, Long lostFoundId) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", chatRoom.getId());
        
        // Add user1 details without circular references
        Map<String, Object> user1Map = new HashMap<>();
        user1Map.put("id", chatRoom.getUser1().getUserID());
        user1Map.put("username", chatRoom.getUser1().getUsername());
        response.put("user1", user1Map);
        
        // Add user2 details without circular references
        Map<String, Object> user2Map = new HashMap<>();
        user2Map.put("id", chatRoom.getUser2().getUserID());
        user2Map.put("username", chatRoom.getUser2().getUsername());
        response.put("user2", user2Map);
        
        // Add lostFoundId for reference
        response.put("lostFoundId", lostFoundId);
        response.put("isGroupChat", chatRoom.isGroupChat());
        
        return ResponseEntity.ok(response);
    }
    
    // Helper method to build a group chat room response
    private ResponseEntity<Map<String, Object>> buildGroupChatRoomResponse(ChatRoom chatRoom, Long lostFoundId) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", chatRoom.getId());
        
        // Add creator details
        Map<String, Object> creatorMap = new HashMap<>();
        creatorMap.put("id", chatRoom.getCreator().getUserID());
        creatorMap.put("username", chatRoom.getCreator().getUsername());
        response.put("creator", creatorMap);
        
        // Add user1 and user2 for compatibility with frontend
        Map<String, Object> user1Map = new HashMap<>();
        user1Map.put("id", chatRoom.getUser1().getUserID());
        user1Map.put("username", chatRoom.getUser1().getUsername());
        response.put("user1", user1Map);
        
        Map<String, Object> user2Map = new HashMap<>();
        user2Map.put("id", chatRoom.getUser2().getUserID());
        user2Map.put("username", chatRoom.getUser2().getUsername());
        response.put("user2", user2Map);
        
        // Add participants list
        List<ChatParticipant> participants = chatService.getChatRoomParticipants(chatRoom.getId());
        List<Map<String, Object>> participantsList = new ArrayList<>();
        
        for (ChatParticipant participant : participants) {
            Map<String, Object> participantMap = new HashMap<>();
            participantMap.put("id", participant.getUser().getUserID());
            participantMap.put("username", participant.getUser().getUsername());
            participantMap.put("joinedAt", participant.getJoinedAt().toString());
            participantsList.add(participantMap);
        }
        
        response.put("participants", participantsList);
        response.put("isGroupChat", true);
        response.put("lostFoundId", lostFoundId);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoom>> getUserChatRooms(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = Long.parseLong(userDetails.getId());
        List<ChatRoom> chatRooms = chatService.getUserChatRooms(userId);
        return ResponseEntity.ok(chatRooms);
    }

    @GetMapping("/messages/{roomId}")
    public ResponseEntity<List<Message>> getChatMessages(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        // Mark messages as read for the current user
        chatService.markMessagesAsRead(roomId, Long.parseLong(userDetails.getId()));
        
        // Get messages
        List<Message> messages = chatService.getChatMessages(roomId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<Map<String, Object>> getChatRoomById(@PathVariable Long roomId) {
        try {
            ChatRoom chatRoom = chatService.getChatRoomById(roomId);
            
            // Create a response based on group chat status
            if (chatRoom.isGroupChat()) {
                return buildGroupChatRoomResponse(chatRoom, chatRoom.getLostAndFound().getId());
            } else {
                return buildChatRoomResponse(chatRoom, chatRoom.getLostAndFound().getId());
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to get chat room: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/unread/{roomId}")
    public ResponseEntity<Map<String, Long>> getUnreadMessagesCount(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        long count = chatService.countUnreadMessages(roomId, Long.parseLong(userDetails.getId()));
        Map<String, Long> response = new HashMap<>();
        response.put("unreadCount", count);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/mark-read/{roomId}")
    public ResponseEntity<Map<String, Integer>> markMessagesAsRead(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        int markedCount = chatService.markMessagesAsRead(roomId, Long.parseLong(userDetails.getId()));
        Map<String, Integer> response = new HashMap<>();
        response.put("markedCount", markedCount);
        return ResponseEntity.ok(response);
    }
    
    // Endpoints for participant management
    @PostMapping("/room/{roomId}/participants")
    public ResponseEntity<?> addParticipant(
            @PathVariable Long roomId,
            @RequestParam Long userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        try {
            boolean added = chatService.addParticipantToRoom(roomId, userId);
            if (added) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(400).body("User is already a participant");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to add participant: " + e.getMessage());
        }
    }

    @DeleteMapping("/room/{roomId}/participants/{userId}")
    public ResponseEntity<?> removeParticipant(
            @PathVariable Long roomId,
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        try {
            boolean removed = chatService.removeParticipantFromRoom(roomId, userId);
            if (removed) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(400).body("Failed to remove participant");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to remove participant: " + e.getMessage());
        }
    }

    @GetMapping("/room/{roomId}/participants")
    public ResponseEntity<List<Map<String, Object>>> getRoomParticipants(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        try {
            List<ChatParticipant> participants = chatService.getChatRoomParticipants(roomId);
            List<Map<String, Object>> participantsList = new ArrayList<>();
            
            for (ChatParticipant participant : participants) {
                Map<String, Object> participantMap = new HashMap<>();
                participantMap.put("id", participant.getUser().getUserID());
                participantMap.put("username", participant.getUser().getUsername());
                participantMap.put("joinedAt", participant.getJoinedAt().toString());
                participantsList.add(participantMap);
            }
            
            return ResponseEntity.ok(participantsList);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
} 
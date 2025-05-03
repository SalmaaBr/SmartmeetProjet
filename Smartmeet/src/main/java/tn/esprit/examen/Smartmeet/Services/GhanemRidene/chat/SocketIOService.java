package tn.esprit.examen.Smartmeet.Services.GhanemRidene.chat;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.BroadcastOperations;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.chat.ChatRoom;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.chat.Message;
import tn.esprit.examen.Smartmeet.entities.Users.Users;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SocketIOService {

    private final SocketIOServer socketIOServer;
    private ChatService chatService;
    
    // Map to store client session to room ID
    private final Map<String, Long> clientRooms = new ConcurrentHashMap<>();

    @Autowired
    public SocketIOService(SocketIOServer socketIOServer) {
        this.socketIOServer = socketIOServer;
        
        // Configure CORS or any other global settings
        socketIOServer.getConfiguration().setTransports(
                com.corundumstudio.socketio.Transport.WEBSOCKET
        );
    }
    
    @Autowired
    public void setChatService(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostConstruct
    private void init() {
        // Register event listeners
        socketIOServer.addConnectListener(onConnected());
        socketIOServer.addDisconnectListener(onDisconnected());
        
        // Set up event handlers
        setupEventListeners();
        
        // Start the server
        socketIOServer.start();
        
        log.info("Socket.IO server started. Listening on port {}", 
                socketIOServer.getConfiguration().getPort());
    }

    @PreDestroy
    private void destroy() {
        if (socketIOServer != null) {
            socketIOServer.stop();
            log.info("Socket.IO server stopped");
        }
    }

    private ConnectListener onConnected() {
        return client -> {
            if (client != null && client.getSessionId() != null) {
                log.info("Client connected: " + client.getSessionId());
            } else {
                log.warn("Received connection with null client or session ID");
            }
        };
    }

    private DisconnectListener onDisconnected() {
        return client -> {
            if (client != null && client.getSessionId() != null) {
                log.info("Client disconnected: " + client.getSessionId());
                clientRooms.remove(client.getSessionId().toString());
            } else {
                log.warn("Received disconnection with null client or session ID");
            }
        };
    }

    private void setupEventListeners() {
        // Listen for join room event
        socketIOServer.addEventListener("join-room", Map.class, onJoinRoom());
        log.info("Registered 'join-room' event listener");
        
        // Listen for send message event
        socketIOServer.addEventListener("send-message", Map.class, onSendMessage());
        log.info("Registered 'send-message' event listener");
        
        // Listen for typing indicator event
        socketIOServer.addEventListener("typing-indicator", Map.class, onTypingIndicator());
        log.info("Registered 'typing-indicator' event listener");
    }

    private DataListener<Map> onJoinRoom() {
        return (client, data, ackRequest) -> {
            try {
                if (client == null || client.getSessionId() == null) {
                    log.warn("Null client or session ID in join-room event");
                    return;
                }
                
                // Extract room ID and user info
                Long roomId = Long.parseLong(data.get("roomId").toString());
                Long userId = data.containsKey("userId") ? 
                    Long.parseLong(data.get("userId").toString()) : null;
                String username = data.containsKey("username") ? 
                    data.get("username").toString() : "Anonymous";
                
                // Store client's room
                clientRooms.put(client.getSessionId().toString(), roomId);
                
                // Join the room
                String roomName = "room:" + roomId;
                client.joinRoom(roomName);
                
                log.info("Client {} (user: {}) joined room {} (room name: {})", 
                    client.getSessionId(), username, roomId, roomName);
                
                // If user data was provided and this is a group chat, add the user as a participant
                if (userId != null) {
                    try {
                        ChatRoom chatRoom = chatService.getChatRoomById(roomId);
                        if (chatRoom.isGroupChat()) {
                            chatService.addParticipantToRoom(roomId, userId);
                            log.info("Added user {} ({}) as participant to group chat room {}", 
                                userId, username, roomId);
                            
                            // Notify other participants that a new user joined
                            Map<String, Object> joinNotification = new HashMap<>();
                            joinNotification.put("userId", userId);
                            joinNotification.put("username", username);
                            joinNotification.put("action", "joined");
                            
                            socketIOServer.getRoomOperations(roomName)
                                .sendEvent("participant.joined", joinNotification);
                        }
                    } catch (Exception e) {
                        log.warn("Could not add user {} as participant to chat room {}: {}", 
                            userId, roomId, e.getMessage());
                    }
                }
                
                // Send acknowledgment if requested
                if (ackRequest.isAckRequested()) {
                    ackRequest.sendAckData(new HashMap<String, Object>() {{
                        put("status", "success");
                        put("message", "Joined room " + roomId);
                    }});
                }
            } catch (Exception e) {
                log.error("Error joining room", e);
                // Send error acknowledgment if requested
                if (ackRequest.isAckRequested()) {
                    ackRequest.sendAckData(new HashMap<String, Object>() {{
                        put("status", "error");
                        put("message", "Failed to join room: " + e.getMessage());
                    }});
                }
            }
        };
    }

    private DataListener<Map> onSendMessage() {
        return (client, data, ackRequest) -> {
            try {
                if (client == null || client.getSessionId() == null) {
                    log.warn("Null client or session ID in send-message event");
                    return;
                }
                
                String content = (String) data.get("content");
                String senderId = (String) data.get("senderId");
                String roomId = (String) data.get("roomId");
                
                log.info("Received message from client {}: room={}, sender={}, content={}", 
                        client.getSessionId(), roomId, senderId, content);
                
                // Send the message via chat service
                Message message = chatService.sendMessage(
                    Long.parseLong(senderId), 
                    Long.parseLong(roomId), 
                    content
                );
                
                // Create a simplified map to avoid serialization issues
                Map<String, Object> messageData = new HashMap<>();
                messageData.put("id", message.getId());
                messageData.put("content", message.getContent());
                messageData.put("senderId", message.getSender().getUserID());
                messageData.put("senderUsername", message.getSender().getUsername());
                messageData.put("receiverId", message.getReceiver().getUserID());
                messageData.put("timestamp", message.getTimestamp().toString());
                messageData.put("read", message.isRead());
                
                // Log before broadcast
                log.info("Broadcasting message to room:{} - {}", roomId, messageData);
                
                // Broadcast to the chat room topic
                String roomEventName = "chat." + roomId;
                socketIOServer.getRoomOperations("room:" + roomId)
                    .sendEvent(roomEventName, messageData);
                
                log.info("Message broadcast complete to room:{} with event {}", roomId, roomEventName);
                
                // If AckRequest is not null, send acknowledgment
                if (ackRequest.isAckRequested()) {
                    ackRequest.sendAckData(new HashMap<String, Object>() {{
                        put("status", "success");
                        put("messageId", message.getId());
                    }});
                }
            } catch (Exception e) {
                log.error("Error sending message", e);
                // Send error acknowledgment if requested
                if (ackRequest.isAckRequested()) {
                    ackRequest.sendAckData(new HashMap<String, Object>() {{
                        put("status", "error");
                        put("message", "Failed to send message: " + e.getMessage());
                    }});
                }
            }
        };
    }

    private DataListener<Map> onTypingIndicator() {
        return (client, data, ackRequest) -> {
            try {
                if (client == null || client.getSessionId() == null) {
                    log.warn("Null client or session ID in typing-indicator event");
                    return;
                }
                
                String roomId = (String) data.get("roomId");
                String userId = (String) data.get("userId");
                String username = (String) data.get("username");
                boolean typing = (boolean) data.get("typing");
                
                log.info("Typing indicator from user {} in room {}: typing={}", username, roomId, typing);
                
                // Broadcast typing indicator to the room
                String roomEventName = "chat." + roomId + ".typing";
                Map<String, Object> typingData = new HashMap<>();
                typingData.put("userId", userId);
                typingData.put("username", username);
                typingData.put("typing", typing);
                
                socketIOServer.getRoomOperations("room:" + roomId)
                    .sendEvent(roomEventName, typingData);
                
                log.info("Typing indicator broadcast to room:{} with event {}", roomId, roomEventName);
            } catch (Exception e) {
                log.error("Error sending typing indicator", e);
            }
        };
    }
    
    // Broadcasting methods for use from other services
    
    public void broadcastMessageToRoom(Long roomId, Message message) {
        try {
            // Create a simplified map to avoid serialization issues
            Map<String, Object> messageData = new HashMap<>();
            messageData.put("id", message.getId());
            messageData.put("content", message.getContent());
            messageData.put("senderId", message.getSender().getUserID());
            messageData.put("senderUsername", message.getSender().getUsername());
            messageData.put("receiverId", message.getReceiver().getUserID());
            messageData.put("timestamp", message.getTimestamp().toString());
            messageData.put("read", message.isRead());
            
            socketIOServer.getRoomOperations("room:" + roomId)
                .sendEvent("chat." + roomId, messageData);
                
            log.info("Message broadcast to room:{}", roomId);
        } catch (Exception e) {
            log.error("Error broadcasting message to room {}", roomId, e);
        }
    }
    
    public void broadcastTypingIndicator(Long roomId, Long userId, String username, boolean typing) {
        try {
            socketIOServer.getRoomOperations("room:" + roomId)
                .sendEvent("chat." + roomId + ".typing", new HashMap<String, Object>() {{
                    put("userId", userId);
                    put("username", username);
                    put("typing", typing);
                }});
        } catch (Exception e) {
            log.error("Error broadcasting typing indicator to room {}", roomId, e);
        }
    }
    
    public void broadcastParticipantJoined(Long roomId, Users user) {
        try {
            Map<String, Object> joinData = new HashMap<>();
            joinData.put("userId", user.getUserID());
            joinData.put("username", user.getUsername());
            joinData.put("action", "joined");
            
            socketIOServer.getRoomOperations("room:" + roomId)
                .sendEvent("participant.joined", joinData);
                
            log.info("Participant joined broadcast to room:{}", roomId);
        } catch (Exception e) {
            log.error("Error broadcasting participant joined to room {}", roomId, e);
        }
    }
    
    public void broadcastParticipantLeft(Long roomId, Users user) {
        try {
            Map<String, Object> leftData = new HashMap<>();
            leftData.put("userId", user.getUserID());
            leftData.put("username", user.getUsername());
            leftData.put("action", "left");
            
            socketIOServer.getRoomOperations("room:" + roomId)
                .sendEvent("participant.left", leftData);
                
            log.info("Participant left broadcast to room:{}", roomId);
        } catch (Exception e) {
            log.error("Error broadcasting participant left to room {}", roomId, e);
        }
    }
    
    /**
     * Gets the BroadcastOperations for a specific room.
     * This method exposes the underlying socketIOServer's room operations functionality.
     * 
     * @param room The room name
     * @return BroadcastOperations for the specified room
     */
    public BroadcastOperations getRoomOperations(String room) {
        return socketIOServer.getRoomOperations(room);
    }
} 
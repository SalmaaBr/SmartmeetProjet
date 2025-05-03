package tn.esprit.examen.Smartmeet.Services.GhanemRidene.chat;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.LostAndFound;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.chat.ChatParticipant;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.chat.ChatRoom;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.chat.Message;
import tn.esprit.examen.Smartmeet.entities.Users.Users;
import tn.esprit.examen.Smartmeet.repositories.GhanemRidene.LostAndFoundRepository;
import tn.esprit.examen.Smartmeet.repositories.GhanemRidene.chat.ChatParticipantRepository;
import tn.esprit.examen.Smartmeet.repositories.GhanemRidene.chat.ChatRoomRepository;
import tn.esprit.examen.Smartmeet.repositories.GhanemRidene.chat.MessageRepository;
import tn.esprit.examen.Smartmeet.repositories.Users.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final LostAndFoundRepository lostAndFoundRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private SocketIOService socketIOService;

    @Autowired
    public ChatServiceImpl(
            ChatRoomRepository chatRoomRepository,
            MessageRepository messageRepository,
            UserRepository userRepository,
            LostAndFoundRepository lostAndFoundRepository,
            ChatParticipantRepository chatParticipantRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.lostAndFoundRepository = lostAndFoundRepository;
        this.chatParticipantRepository = chatParticipantRepository;
    }
    
    @Autowired
    public void setSocketIOService(SocketIOService socketIOService) {
        this.socketIOService = socketIOService;
    }

    @Override
    @Transactional
    public ChatRoom findOrCreateChatRoom(Long currentUserId, Long itemCreatorId, Long lostFoundId) {
        // Find users
        Users currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("Current user not found"));
        
        Users itemCreator = userRepository.findById(itemCreatorId)
                .orElseThrow(() -> new EntityNotFoundException("Item creator not found"));
        
        // Find lost and found item
        LostAndFound lostAndFound = lostAndFoundRepository.findById(lostFoundId)
                .orElseThrow(() -> new EntityNotFoundException("Lost and found item not found"));
        
        // Check if a chat room already exists
        Optional<ChatRoom> existingChatRoom = chatRoomRepository.findChatRoomByUsersAndLostFound(
                currentUserId, 
                itemCreatorId, 
                lostFoundId
        );
        
        // Return existing or create new
        return existingChatRoom.orElseGet(() -> {
            ChatRoom newChatRoom = new ChatRoom(currentUser, itemCreator, lostAndFound);
            return chatRoomRepository.save(newChatRoom);
        });
    }

    @Override
    public List<ChatRoom> getUserChatRooms(Long userId) {
        // Find user to verify existence
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        
        // Get all chat rooms for the user
        return chatRoomRepository.findChatRoomsByUser(userId);
    }

    @Override
    public ChatRoom getChatRoomById(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("Chat room not found"));
    }

    @Override
    @Transactional
    public Message sendMessage(Long senderId, Long chatRoomId, String content) {
        // Find sender
        Users sender = userRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("Sender not found"));
        
        // Find chat room
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("Chat room not found"));
        
        Users receiver;
        
        if (chatRoom.isGroupChat()) {
            // For group chats, set receiver to the same as sender
            // Messages will be broadcast to all participants
            receiver = sender;
        } else {
            // For one-to-one chats, determine the other user
            receiver = chatRoom.getUser1().getUserID().equals(senderId) 
                    ? chatRoom.getUser2() 
                    : chatRoom.getUser1();
        }
        
        // Create and save message
        Message message = new Message(
                content, 
                sender, 
                receiver, 
                chatRoom, 
                chatRoom.getLostAndFound()
        );
        
        message = messageRepository.save(message);
        
        // Broadcast message via Socket.IO
        socketIOService.broadcastMessageToRoom(chatRoomId, message);
        
        return message;
    }

    @Override
    public List<Message> getChatMessages(Long chatRoomId) {
        // Find chat room
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("Chat room not found"));
        
        // Get messages
        return messageRepository.findByChatRoomOrderByTimestampDesc(chatRoom);
    }

    @Override
    @Transactional
    public int markMessagesAsRead(Long chatRoomId, Long userId) {
        int updatedCount = messageRepository.markMessagesAsRead(chatRoomId, userId);
        
        // Get chat room to broadcast updated read status
        if (updatedCount > 0) {
            chatRoomRepository.findById(chatRoomId).ifPresent(chatRoom -> {
                String username = userRepository.findById(userId)
                        .map(Users::getUsername)
                        .orElse("Unknown");
                        
                // Notify all clients in the room that messages have been read
                Map<String, Object> readUpdate = new HashMap<>();
                readUpdate.put("userId", userId);
                readUpdate.put("username", username);
                readUpdate.put("roomId", chatRoomId);
                readUpdate.put("messagesRead", updatedCount);
                
                socketIOService.getRoomOperations("room:" + chatRoomId)
                        .sendEvent("messages.read", readUpdate);
            });
        }
        
        return updatedCount;
    }

    @Override
    public long countUnreadMessages(Long chatRoomId, Long userId) {
        // Verify chat room exists
        chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("Chat room not found"));
        
        // Count unread messages
        return messageRepository.countUnreadMessagesInChatRoom(chatRoomId, userId);
    }
    
    @Override
    @Transactional
    public ChatRoom findOrCreateGroupChatRoom(Long lostFoundId, Long currentUserId) {
        // Find the lost and found item
        LostAndFound lostAndFound = lostAndFoundRepository.findById(lostFoundId)
                .orElseThrow(() -> new EntityNotFoundException("Lost and found item not found"));
        
        // Find the current user
        Users currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        
        // Check if a group chat room already exists for this lost and found item
        Optional<ChatRoom> existingChatRoom = chatRoomRepository.findGroupChatRoomByLostFoundId(lostFoundId);
        
        // Return existing or create new
        ChatRoom chatRoom = existingChatRoom.orElseGet(() -> {
            ChatRoom newChatRoom = new ChatRoom(currentUser, lostAndFound, true);
            return chatRoomRepository.save(newChatRoom);
        });
        
        // Add the current user as a participant if not already present
        boolean added = addParticipantIfNotExists(chatRoom, currentUser);
        
        // If user was added (not already present), broadcast join event
        if (added && existingChatRoom.isPresent()) {
            socketIOService.broadcastParticipantJoined(chatRoom.getId(), currentUser);
        }
        
        return chatRoom;
    }
    
    private boolean addParticipantIfNotExists(ChatRoom chatRoom, Users user) {
        Optional<ChatParticipant> existingParticipant = 
            chatParticipantRepository.findByChatRoomIdAndUserId(chatRoom.getId(), user.getUserID());
        
        if (existingParticipant.isEmpty()) {
            ChatParticipant participant = new ChatParticipant(chatRoom, user);
            chatParticipantRepository.save(participant);
            return true; // Participant was added
        }
        
        return false; // Participant already existed
    }
    
    @Override
    @Transactional
    public boolean addParticipantToRoom(Long chatRoomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("Chat room not found"));
        
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        
        // Check if user is already a participant
        Optional<ChatParticipant> existingParticipant = 
            chatParticipantRepository.findByChatRoomIdAndUserId(chatRoomId, userId);
        
        if (existingParticipant.isPresent()) {
            return false; // User is already a participant
        }
        
        // Add user as participant
        ChatParticipant participant = new ChatParticipant(chatRoom, user);
        chatParticipantRepository.save(participant);
        
        // Broadcast participant joined event
        socketIOService.broadcastParticipantJoined(chatRoomId, user);
        
        return true;
    }
    
    @Override
    @Transactional
    public boolean removeParticipantFromRoom(Long chatRoomId, Long userId) {
        // Verify chatRoom exists
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("Chat room not found"));
        
        // Verify user exists
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        
        // Check if participant exists
        Optional<ChatParticipant> existingParticipant = 
            chatParticipantRepository.findByChatRoomIdAndUserId(chatRoomId, userId);
            
        if (existingParticipant.isEmpty()) {
            return false; // User is not a participant
        }
        
        // Remove participant
        chatParticipantRepository.deleteByChatRoomIdAndUserId(chatRoomId, userId);
        
        // Broadcast participant left event
        socketIOService.broadcastParticipantLeft(chatRoomId, user);
        
        return true;
    }
    
    @Override
    public List<ChatParticipant> getChatRoomParticipants(Long chatRoomId) {
        // Verify chatRoom exists
        chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("Chat room not found"));
        
        return chatParticipantRepository.findByChatRoomId(chatRoomId);
    }
} 
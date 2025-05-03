package tn.esprit.examen.Smartmeet.Services.GhanemRidene.chat;

import tn.esprit.examen.Smartmeet.entities.GhanemRidene.chat.ChatParticipant;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.chat.ChatRoom;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.chat.Message;

import java.util.List;

public interface ChatService {
    
    // Chat Room operations
    ChatRoom findOrCreateChatRoom(Long currentUserId, Long itemCreatorId, Long lostFoundId);
    List<ChatRoom> getUserChatRooms(Long userId);
    ChatRoom getChatRoomById(Long chatRoomId);
    
    // Message operations
    Message sendMessage(Long senderId, Long chatRoomId, String content);
    List<Message> getChatMessages(Long chatRoomId);
    int markMessagesAsRead(Long chatRoomId, Long userId);
    long countUnreadMessages(Long chatRoomId, Long userId);
    
    // Group chat operations
    ChatRoom findOrCreateGroupChatRoom(Long lostFoundId, Long currentUserId);
    boolean addParticipantToRoom(Long chatRoomId, Long userId);
    boolean removeParticipantFromRoom(Long chatRoomId, Long userId);
    List<ChatParticipant> getChatRoomParticipants(Long chatRoomId);
} 
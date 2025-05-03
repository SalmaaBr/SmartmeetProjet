package tn.esprit.examen.Smartmeet.repositories.GhanemRidene.chat;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.chat.ChatRoom;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.chat.Message;
import tn.esprit.examen.Smartmeet.entities.Users.Users;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    // Find messages by chat room, ordered by timestamp (newest first)
    List<Message> findByChatRoomOrderByTimestampDesc(ChatRoom chatRoom);
    
    // Find messages by chat room with pagination
    Page<Message> findByChatRoomOrderByTimestampDesc(ChatRoom chatRoom, Pageable pageable);
    
    // Count unread messages for a specific user in a chat room
    @Query("SELECT COUNT(m) FROM Message m WHERE m.chatRoom.id = :chatRoomId " +
           "AND m.receiver.userID = :userId AND m.isRead = false")
    Long countUnreadMessagesInChatRoom(@Param("chatRoomId") Long chatRoomId, @Param("userId") Long userId);
    
    // Mark all messages as read for a specific user in a chat room
    @Modifying
    @Query("UPDATE Message m SET m.isRead = true WHERE m.chatRoom.id = :chatRoomId " +
           "AND m.receiver.userID = :userId AND m.isRead = false")
    int markMessagesAsRead(@Param("chatRoomId") Long chatRoomId, @Param("userId") Long userId);
    
    // Delete all messages associated with a chat room
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM message WHERE chat_room_id = :chatRoomId", nativeQuery = true)
    void deleteByChatRoomId(@Param("chatRoomId") Long chatRoomId);
    
    // Delete all messages associated with a lost and found item
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM message WHERE lost_found_id = :lostFoundId", nativeQuery = true)
    void deleteByLostFoundId(@Param("lostFoundId") Long lostFoundId);
} 
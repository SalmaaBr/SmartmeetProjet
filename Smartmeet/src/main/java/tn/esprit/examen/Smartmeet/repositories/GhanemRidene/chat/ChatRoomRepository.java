package tn.esprit.examen.Smartmeet.repositories.GhanemRidene.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.LostAndFound;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.chat.ChatRoom;
import tn.esprit.examen.Smartmeet.entities.Users.Users;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    
    // Find a chat room between two users for a specific lost and found item
    @Query("SELECT c FROM ChatRoom c WHERE c.lostAndFound.id = :lostFoundId " +
            "AND ((c.user1.userID = :user1Id AND c.user2.userID = :user2Id) " +
            "OR (c.user1.userID = :user2Id AND c.user2.userID = :user1Id)) " +
            "AND c.isGroupChat = false")
    Optional<ChatRoom> findChatRoomByUsersAndLostFound(
            @Param("user1Id") Long user1Id,
            @Param("user2Id") Long user2Id,
            @Param("lostFoundId") Long lostFoundId);
    
    // Find all chat rooms for a specific user
    @Query("SELECT c FROM ChatRoom c WHERE c.user1.userID = :userId OR c.user2.userID = :userId " +
           "OR EXISTS (SELECT p FROM ChatParticipant p WHERE p.chatRoom = c AND p.user.userID = :userId)")
    List<ChatRoom> findChatRoomsByUser(@Param("userId") Long userId);
    
    // Find all chat rooms for a specific lost and found item
    @Query("SELECT c FROM ChatRoom c WHERE c.lostAndFound = :lostAndFound")
    List<ChatRoom> findByLostAndFound(@Param("lostAndFound") LostAndFound lostAndFound);
    
    // Find chat rooms for a lost and found item that are group chats
    @Query("SELECT c FROM ChatRoom c WHERE c.lostAndFound = :lostAndFound AND c.isGroupChat = :isGroupChat")
    List<ChatRoom> findByLostFoundAndIsGroupChat(
            @Param("lostAndFound") LostAndFound lostAndFound, 
            @Param("isGroupChat") boolean isGroupChat);
    
    // Find group chat room by lost and found item ID
    @Query("SELECT c FROM ChatRoom c WHERE c.lostAndFound.id = :lostFoundId AND c.isGroupChat = true")
    Optional<ChatRoom> findGroupChatRoomByLostFoundId(@Param("lostFoundId") Long lostFoundId);
    
    // Delete all chat rooms associated with a lost and found item using native SQL
    // This avoids potential JPA issues with invalid user references
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM chat_room WHERE lost_found_id = :lostFoundId", nativeQuery = true)
    void deleteByLostFoundId(@Param("lostFoundId") Long lostFoundId);
} 
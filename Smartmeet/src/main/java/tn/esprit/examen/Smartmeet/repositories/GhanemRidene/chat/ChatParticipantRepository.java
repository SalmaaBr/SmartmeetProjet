package tn.esprit.examen.Smartmeet.repositories.GhanemRidene.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.chat.ChatParticipant;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {
    
    List<ChatParticipant> findByChatRoomId(Long chatRoomId);
    
    @Query("SELECT cp FROM ChatParticipant cp WHERE cp.chatRoom.id = :chatRoomId AND cp.user.userID = :userId")
    Optional<ChatParticipant> findByChatRoomIdAndUserId(
            @Param("chatRoomId") Long chatRoomId, 
            @Param("userId") Long userId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM ChatParticipant cp WHERE cp.chatRoom.id = :chatRoomId AND cp.user.userID = :userId")
    void deleteByChatRoomIdAndUserId(
            @Param("chatRoomId") Long chatRoomId, 
            @Param("userId") Long userId);
            
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM chat_participant WHERE chat_room_id = :chatRoomId", nativeQuery = true)
    void deleteByChatRoomId(@Param("chatRoomId") Long chatRoomId);
    
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM chat_participant WHERE chat_room_id IN (SELECT id FROM chat_room WHERE lost_found_id = :lostFoundId)", nativeQuery = true)
    void deleteByLostFoundId(@Param("lostFoundId") Long lostFoundId);
} 
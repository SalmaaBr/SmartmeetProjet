package tn.esprit.examen.Smartmeet.Services.GhanemRidene;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.LostAndFound;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.chat.ChatRoom;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.dto.LostAndFoundRequest;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.dto.LostAndFoundResponse;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Event;
import tn.esprit.examen.Smartmeet.entities.Users.TypeUserRole;
import tn.esprit.examen.Smartmeet.entities.Users.Users;
import tn.esprit.examen.Smartmeet.repositories.GhanemRidene.LostAndFoundRepository;
import tn.esprit.examen.Smartmeet.repositories.GhanemRidene.chat.ChatParticipantRepository;
import tn.esprit.examen.Smartmeet.repositories.GhanemRidene.chat.ChatRoomRepository;
import tn.esprit.examen.Smartmeet.repositories.GhanemRidene.chat.MessageRepository;
import tn.esprit.examen.Smartmeet.repositories.SalmaBenRomdhan.IEventRepository;
import tn.esprit.examen.Smartmeet.repositories.Users.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LostAndFoundServiceImpl implements LostAndFoundService {

    private final LostAndFoundRepository lostAndFoundRepository;
    private final IEventRepository eventRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final ChatParticipantRepository chatParticipantRepository;

    @Override
    @Transactional
    public LostAndFoundResponse createLostAndFound(LostAndFoundRequest request, Long creatorId) {
        // Get event
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + request.getEventId()));
        
        // Get creator
        Users creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + creatorId));
        
        // Create new lost and found item
        LostAndFound lostAndFound = new LostAndFound();
        lostAndFound.setTitle(request.getTitle());
        lostAndFound.setDescription(request.getDescription());
        lostAndFound.setType(request.getType());
        lostAndFound.setCreatedAt(LocalDateTime.now());
        lostAndFound.setImageUrl(request.getImageUrl());
        lostAndFound.setEvent(event);
        lostAndFound.setCreator(creator);
        
        // Save to database
        LostAndFound savedItem = lostAndFoundRepository.save(lostAndFound);
        
        // Notify all event participants
        notificationService.notifyEventParticipants(savedItem);
        
        return convertToDto(savedItem);
    }

    @Override
    public List<LostAndFoundResponse> getAllLostAndFoundByEvent(Long eventId) {
        List<LostAndFound> items = lostAndFoundRepository.findByEventId(eventId);
        
        return items.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LostAndFoundResponse updateLostAndFound(Long id, LostAndFoundRequest request, Long userId) {
        LostAndFound existingItem = lostAndFoundRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lost and found item not found with id: " + id));
        
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        
        // Check if user is the creator or an admin
        boolean isAdmin = user.getUserRole() != null && 
                           user.getUserRole().stream()
                           .anyMatch(role -> role.name().equals("ADMIN") || role.name().equals("SUPER_ADMIN"));
        
        if (!existingItem.getCreator().getUserID().equals(userId) && !isAdmin) {
            throw new RuntimeException("You are not authorized to update this item");
        }
        
        // If event ID is different, get the new event
        if (!existingItem.getEvent().getId().equals(request.getEventId())) {
            Event newEvent = eventRepository.findById(request.getEventId())
                    .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + request.getEventId()));
            existingItem.setEvent(newEvent);
        }
        
        // Update fields
        existingItem.setTitle(request.getTitle());
        existingItem.setDescription(request.getDescription());
        existingItem.setType(request.getType());
        
        // Only update imageUrl if provided
        if (request.getImageUrl() != null && !request.getImageUrl().isEmpty()) {
            existingItem.setImageUrl(request.getImageUrl());
        }
        
        LostAndFound updatedItem = lostAndFoundRepository.save(existingItem);
        return convertToDto(updatedItem);
    }

    @Override
    @Transactional
    public void deleteLostAndFound(Long id, Long userId) {
        LostAndFound item = lostAndFoundRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lost and found item not found with id: " + id));
        
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        
        // Check if user is the creator or an admin
        boolean isAdmin = user.getUserRole() != null && 
                           user.getUserRole().stream()
                           .anyMatch(role -> role.name().equals("ADMIN") || role.name().equals("SUPER_ADMIN"));
        
        if (!item.getCreator().getUserID().equals(userId) && !isAdmin) {
            throw new RuntimeException("You are not authorized to delete this item");
        }
        
        try {
            // Delete in proper order to respect foreign key constraints
            
            // 1. First delete chat participants to remove references to chat rooms
            chatParticipantRepository.deleteByLostFoundId(id);
            
            // 2. Then delete all messages related to this lost and found item
            messageRepository.deleteByLostFoundId(id);
            
            // 3. Finally delete all chat rooms associated with this lost and found item
            chatRoomRepository.deleteByLostFoundId(id);
            
        } catch (Exception e) {
            // Log the error but continue with the deletion of the lost and found item
            System.err.println("Error deleting associated chat data: " + e.getMessage());
            // In a production environment, you might want to log this properly
        }
        
        // Delete the lost and found item
        lostAndFoundRepository.delete(item);
    }

    @Override
    public LostAndFoundResponse getLostAndFoundById(Long id) {
        LostAndFound item = lostAndFoundRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lost and found item not found with id: " + id));
        
        return convertToDto(item);
    }
    
    private LostAndFoundResponse convertToDto(LostAndFound item) {
        LostAndFoundResponse response = new LostAndFoundResponse();
        response.setId(item.getId());
        response.setTitle(item.getTitle());
        response.setDescription(item.getDescription());
        response.setType(item.getType());
        response.setCreatedAt(item.getCreatedAt());
        response.setImageUrl(item.getImageUrl());
        response.setEventId(item.getEvent().getId());
        response.setEventTitle(item.getEvent().getTitle());
        response.setCreatorId(item.getCreator().getUserID());
        response.setCreatorUsername(item.getCreator().getUsername());
        return response;
    }
} 
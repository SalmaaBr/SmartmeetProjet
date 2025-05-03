package tn.esprit.examen.Smartmeet.Services.GhanemRidene;

import tn.esprit.examen.Smartmeet.entities.GhanemRidene.dto.LostAndFoundRequest;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.dto.LostAndFoundResponse;

import java.util.List;

public interface LostAndFoundService {
    LostAndFoundResponse createLostAndFound(LostAndFoundRequest request, Long creatorId);
    List<LostAndFoundResponse> getAllLostAndFoundByEvent(Long eventId);
    LostAndFoundResponse updateLostAndFound(Long id, LostAndFoundRequest request, Long userId);
    void deleteLostAndFound(Long id, Long userId);
    LostAndFoundResponse getLostAndFoundById(Long id);
} 
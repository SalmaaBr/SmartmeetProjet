package tn.esprit.examen.Smartmeet.Services.GhanemRidene;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.FoundItem;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Event;
import tn.esprit.examen.Smartmeet.entities.Users.Users;
import tn.esprit.examen.Smartmeet.repositories.GhanemRiden.IFoundItemRepository;
import tn.esprit.examen.Smartmeet.repositories.SalmaBenRomdhan.IEventRepository;
import tn.esprit.examen.Smartmeet.repositories.Users.UserRepository;


import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class FoundItemImpl implements IFoundItemServices {
    private final IFoundItemRepository foundItemRepository;
    private final IEventRepository eventRepository;
    private final UserRepository userRepository;


    @Override
    public FoundItem createFoundItem(FoundItem foundItem) {
        return foundItemRepository.save(foundItem);
    }

    @Override
    public FoundItem updateFoundItem(Long id, FoundItem foundItem) {
        foundItem.setId(id);
        return foundItemRepository.save(foundItem);
    }

    @Override
    public void deleteFoundItem(Long id) {
        foundItemRepository.deleteById(id);
    }

    @Override
    public FoundItem getFoundItemById(Long id) {
        Optional<FoundItem> optionalFoundItem = foundItemRepository.findById(id);
        return optionalFoundItem.orElse(null);
    }

    @Override
    public List<FoundItem> getAllFoundItems() {
        return foundItemRepository.findAll();
}

  @Override
  public FoundItem assignFoundItemToEvent(Long foundItemId, Long eventId) {
    FoundItem foundItem = foundItemRepository.findById(foundItemId).orElse(null);
    Event event = eventRepository.findById(eventId).orElse(null);
    if (foundItem != null && event != null) {
      foundItem.setEvent(event);
      return foundItemRepository.save(foundItem);
    }
    return null;
  }

  @Override
  public FoundItem assignFoundItemToUser(Long foundItemId, Long userId) {
    FoundItem foundItem = foundItemRepository.findById(foundItemId).orElse(null);
    Users user = userRepository.findById(userId).orElse(null);
    if (foundItem != null && user != null) {
      foundItem.setFoundByUser(user);
      return foundItemRepository.save(foundItem);
    }
    return null;
  }

}

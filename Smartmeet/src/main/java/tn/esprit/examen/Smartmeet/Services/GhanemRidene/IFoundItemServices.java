package tn.esprit.examen.Smartmeet.Services.GhanemRidene;



import tn.esprit.examen.Smartmeet.entities.GhanemRidene.FoundItem;

import java.util.List;

public interface IFoundItemServices {
    FoundItem createFoundItem(FoundItem foundItem);
    FoundItem updateFoundItem(Long id, FoundItem foundItem);
    void deleteFoundItem(Long id);
    FoundItem getFoundItemById(Long id);
    List<FoundItem> getAllFoundItems();
  FoundItem assignFoundItemToEvent(Long foundItemId, Long eventId);
  FoundItem assignFoundItemToUser(Long foundItemId, Long userId);

}

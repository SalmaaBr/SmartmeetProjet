package tn.esprit.examen.Smartmeet.Services.GhanemRidene;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.Announcement;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Event;
import tn.esprit.examen.Smartmeet.repositories.GhanemRiden.AnnouncementRepository;
import tn.esprit.examen.Smartmeet.repositories.SalmaBenRomdhan.IEventRepository;

import java.util.List;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

  @Autowired
  private AnnouncementRepository announcementRepository;

  @Autowired
  private IEventRepository eventRepository;

  @Override
  public Announcement createAnnouncement(Announcement announcement, Long eventId) {
    // Récupérer l’événement
    Event event = eventRepository.findById(eventId)
      .orElseThrow(() -> new RuntimeException("Événement non trouvé"));

    // Lier l’événement à l’annonce
    announcement.setEvent(event);

    // Sauvegarder l’annonce
    return announcementRepository.save(announcement);
  }

  @Override
  public Announcement getAnnouncementById(Long id) {
    return announcementRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("Annonce non trouvée"));
  }

  @Override
  public List<Announcement> getAllAnnouncements() {
    return announcementRepository.findAll();
  }

  @Override
  public Announcement updateAnnouncement(Announcement announcement) {
    getAnnouncementById(announcement.getId());
    return announcementRepository.save(announcement);
  }

  @Override
  public void deleteAnnouncement(Long id) {
    getAnnouncementById(id);
    announcementRepository.deleteById(id);
  }
}

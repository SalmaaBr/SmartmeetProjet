package tn.esprit.examen.Smartmeet.Services.GhanemRidene;

import tn.esprit.examen.Smartmeet.entities.GhanemRidene.Announcement;

import java.util.List;

public interface AnnouncementService {

  Announcement createAnnouncement(Announcement announcement, Long eventId);

  Announcement getAnnouncementById(Long id);

  List<Announcement> getAllAnnouncements();

  Announcement updateAnnouncement(Announcement announcement);

  void deleteAnnouncement(Long id);
}


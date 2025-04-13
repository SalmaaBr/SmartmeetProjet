package tn.esprit.examen.Smartmeet.controllers.GhanemRidene;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.Smartmeet.Services.GhanemRidene.AnnouncementService;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.Announcement;

import java.util.List;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/announcements")
public class AnnouncementController {

  @Autowired
  private AnnouncementService announcementService;

  @PostMapping("/event/{eventId}")
  public Announcement createAnnouncement(@RequestBody Announcement announcement, @PathVariable Long eventId) {
    return announcementService.createAnnouncement(announcement, eventId);
  }
  @GetMapping("/{id}")
  public Announcement getAnnouncementById(@PathVariable Long id) {
    return announcementService.getAnnouncementById(id);
  }

  @GetMapping
  public List<Announcement> getAllAnnouncements() {
    return announcementService.getAllAnnouncements();
  }

  @PutMapping("/{id}")
  public Announcement updateAnnouncement(@PathVariable Long id, @RequestBody Announcement announcement) {
    announcement.setId(id);
    return announcementService.updateAnnouncement(announcement);
  }

  @DeleteMapping("/{id}")
  public void deleteAnnouncement(@PathVariable Long id) {
    announcementService.deleteAnnouncement(id);
  }
}

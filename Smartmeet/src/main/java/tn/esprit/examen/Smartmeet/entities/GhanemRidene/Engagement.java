package tn.esprit.examen.Smartmeet.entities.GhanemRidene;

import jakarta.persistence.*;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Event;

import java.time.LocalDate;

@Entity
public class Engagement {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private Sponsor sponsor;

  @ManyToOne
  private Event event;

  private String assetType; // "BANNER", "NOTIFICATION", "VIDEO"

  private int views;

  private int clicks;

  private LocalDate date;

  // Getters et setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Sponsor getSponsor() { return sponsor; }
  public void setSponsor(Sponsor sponsor) { this.sponsor = sponsor; }
  public Event getEvent() { return event; }
  public void setEvent(Event event) { this.event = event; }
  public String getAssetType() { return assetType; }
  public void setAssetType(String assetType) { this.assetType = assetType; }
  public int getViews() { return views; }
  public void setViews(int views) { this.views = views; }
  public int getClicks() { return clicks; }
  public void setClicks(int clicks) { this.clicks = clicks; }
  public LocalDate getDate() { return date; }
  public void setDate(LocalDate date) { this.date = date; }
}

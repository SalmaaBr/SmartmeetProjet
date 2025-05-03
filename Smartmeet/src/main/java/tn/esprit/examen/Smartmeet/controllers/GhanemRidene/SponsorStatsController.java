package tn.esprit.examen.Smartmeet.controllers.GhanemRidene;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.examen.Smartmeet.Services.GhanemRidene.SponsorStatsService;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sponsor-stats")
public class SponsorStatsController {

  private final SponsorStatsService sponsorStatsService;

  public SponsorStatsController(SponsorStatsService sponsorStatsService) {
    this.sponsorStatsService = sponsorStatsService;
  }

  @GetMapping("/event/{eventId}")
  public Map<String, Object> getEventStats(@PathVariable Long eventId) {
    return Map.of(
      "amountsBySponsor", sponsorStatsService.getAmountsBySponsor(eventId),
      "statusDistribution", sponsorStatsService.getStatusDistribution(eventId),
      "averageSignatureTime", sponsorStatsService.getAverageSignatureTime(eventId)
    );
  }
}

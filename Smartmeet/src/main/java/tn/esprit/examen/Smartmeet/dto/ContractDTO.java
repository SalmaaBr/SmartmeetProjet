package tn.esprit.examen.Smartmeet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractDTO {
  private Long id;
  private SponsorDTO sponsor;
  private EventDTO event;
  private String terms;
  private Double amount;
  private String signingDate;
  private String expiryDate;
  private String status;
  private String contractPath;
  private String createdAt;
  private String updatedAt;
  private String signature; // Ajout du champ signature

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class SponsorDTO {
    private Long idSponsor;
    private String nom;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class EventDTO {
    private Long id;
    private String title;
  }
}

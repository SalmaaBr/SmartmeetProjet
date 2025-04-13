package tn.esprit.examen.Smartmeet.Services.GhanemRidene;


import tn.esprit.examen.Smartmeet.entities.GhanemRidene.Claim;

import java.util.List;

public interface IClaimServices {
    Claim createClaim(Claim claim);

    Claim updateClaim(Long id, Claim claim);

    void deleteClaim(Long id);

    Claim getClaimById(Long id);

    List<Claim> getAllClaims();

  Claim assignClaimToFoundItem(Long claimId, Long foundItemId);
  Claim assignClaimToUser(Long claimId, Long userId);

}

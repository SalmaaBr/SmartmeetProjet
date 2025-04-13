package tn.esprit.examen.Smartmeet.Services.GhanemRidene;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import tn.esprit.examen.Smartmeet.entities.GhanemRidene.Claim;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.FoundItem;
import tn.esprit.examen.Smartmeet.entities.Users.Users;
import tn.esprit.examen.Smartmeet.repositories.GhanemRiden.IClaimRepository;
import tn.esprit.examen.Smartmeet.repositories.GhanemRiden.IFoundItemRepository;
import tn.esprit.examen.Smartmeet.repositories.Users.UserRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service

public class ClaimServcesImp implements IClaimServices {

    private final IClaimRepository iclaimRepository;

    private final IFoundItemRepository foundItemRepository;
    private final UserRepository userRepository;

    @Override
    public Claim createClaim(Claim claim) {
        return iclaimRepository.save(claim);
    }

    @Override
    public Claim updateClaim(Long id, Claim claim) {
        claim.setId(id);
        return iclaimRepository.save(claim);
    }

    @Override
    public void deleteClaim(Long id) {
        iclaimRepository.deleteById(id);
    }

    @Override
    public Claim getClaimById(Long id) {
        Optional<Claim> optionalClaim = iclaimRepository.findById(id);
        return optionalClaim.orElse(null);
    }

    @Override
    public List<Claim> getAllClaims() {
        return iclaimRepository.findAll();
    }

  @Override
  public Claim assignClaimToFoundItem(Long claimId, Long foundItemId) {
    Claim claim = iclaimRepository.findById(claimId).orElse(null);
    FoundItem foundItem = foundItemRepository.findById(foundItemId).orElse(null);
    if (claim != null && foundItem != null) {
      claim.setFoundItem(foundItem);
      return iclaimRepository.save(claim);
    }
    return null;
  }

  @Override
  public Claim assignClaimToUser(Long claimId, Long userId) {
    Claim claim = iclaimRepository.findById(claimId).orElse(null);
    Users user = userRepository.findById(userId).orElse(null);
    if (claim != null && user != null) {
      claim.setClaimedByUser(user);
      return iclaimRepository.save(claim);
    }
    return null;
  }


}

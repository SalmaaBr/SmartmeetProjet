package tn.esprit.examen.Smartmeet.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.Smartmeet.Services.GhanemRidene.IClaimServices;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.Claim;


import java.util.List;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/claims")

public class ClaimRestController {

    private final IClaimServices claimServices;

    @PostMapping
    public ResponseEntity<Claim> createClaim(@RequestBody Claim claim) {
        Claim createdClaim = claimServices.createClaim(claim);
        return new ResponseEntity<>(createdClaim, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Claim> updateClaim(@PathVariable Long id, @RequestBody Claim claim) {
        Claim updatedClaim = claimServices.updateClaim(id, claim);
        return new ResponseEntity<>(updatedClaim, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClaim(@PathVariable Long id) {
        claimServices.deleteClaim(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Claim> getClaimById(@PathVariable Long id) {
        Claim claim = claimServices.getClaimById(id);
        return claim != null ? new ResponseEntity<>(claim, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping
    public ResponseEntity<List<Claim>> getAllClaims() {
        List<Claim> claims = claimServices.getAllClaims();
        return new ResponseEntity<>(claims, HttpStatus.OK);
    }

  @PostMapping("/{claimId}/assignFoundItem/{foundItemId}")
  public ResponseEntity<Claim> assignClaimToFoundItem(@PathVariable Long claimId, @PathVariable Long foundItemId) {
    Claim claim = claimServices.assignClaimToFoundItem(claimId, foundItemId);
    if (claim != null) {
      return ResponseEntity.ok(claim);
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }

  @PostMapping("/{claimId}/assignUser/{userId}")
  public ResponseEntity<Claim> assignClaimToUser(@PathVariable Long claimId, @PathVariable Long userId) {
    Claim claim = claimServices.assignClaimToUser(claimId, userId);
    if (claim != null) {
      return ResponseEntity.ok(claim);
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }
}


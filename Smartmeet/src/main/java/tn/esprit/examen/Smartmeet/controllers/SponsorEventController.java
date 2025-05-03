package tn.esprit.examen.Smartmeet.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.examen.Smartmeet.Services.SponsorEventService;
import tn.esprit.examen.Smartmeet.dto.SponsorEventAssignmentDTO;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.Contract;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.Sponsor;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Event;
import tn.esprit.examen.Smartmeet.entities.Users.Users;
import tn.esprit.examen.Smartmeet.dto.ContractDTO;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/sponsor-event")
@CrossOrigin(origins = "*")
public class SponsorEventController {

    @Autowired
    private SponsorEventService sponsorEventService;

    @GetMapping("/users/sponsors")
    public ResponseEntity<List<Users>> getUsersWithSponsorRole() {
        return ResponseEntity.ok(sponsorEventService.getUsersWithSponsorRole());
    }

    @PostMapping("/sponsors")
    public ResponseEntity<Sponsor> createSponsor(
            @RequestBody Sponsor sponsor,
            @RequestParam Long responsibleUserId) {
        return ResponseEntity.ok(sponsorEventService.createSponsor(sponsor, responsibleUserId));
    }

    @GetMapping("/events")
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(sponsorEventService.getAllEvents());
    }

    @PostMapping(value = "/assign", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Contract> assignSponsorToEvent(
            @RequestParam Long sponsorId,
            @RequestParam Long eventId,
            @RequestParam("contract") MultipartFile contractFile,
            @RequestParam String terms,
            @RequestParam Double amount,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime expiryDate) throws IOException {
        return ResponseEntity.ok(sponsorEventService.assignSponsorToEvent(sponsorId, eventId, contractFile, terms, amount, expiryDate));
    }

    @PostMapping("/simple-assign")
    public ResponseEntity<Contract> simpleAssignSponsorToEvent(@RequestBody SponsorEventAssignmentDTO assignmentDTO) {
        return ResponseEntity.ok(sponsorEventService.simpleAssignSponsorToEvent(
            assignmentDTO.getSponsorId(),
            assignmentDTO.getEventId(),
            assignmentDTO.getTerms(),
            assignmentDTO.getAmount(),
            LocalDateTime.parse(assignmentDTO.getExpiryDate())
        ));
    }

    @GetMapping("/sponsor/{sponsorId}/events")
    public ResponseEntity<Set<Event>> getSponsorEvents(@PathVariable Long sponsorId) {
        return ResponseEntity.ok(sponsorEventService.getSponsorEvents(sponsorId));
    }

    @GetMapping("/contract/{contractId}")
    public ResponseEntity<byte[]> getContractFile(@PathVariable Long contractId) throws IOException {
        byte[] fileContent = sponsorEventService.getContractFile(contractId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "contract.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(fileContent);
    }

    @GetMapping("/sponsor/{sponsorId}/contracts")
    public ResponseEntity<List<Contract>> getSponsorContracts(@PathVariable Long sponsorId) {
        return ResponseEntity.ok(sponsorEventService.getSponsorContracts(sponsorId));
    }

    @GetMapping("/check-assignment/{sponsorId}/{eventId}")
    public ResponseEntity<Boolean> checkExistingAssignment(
            @PathVariable Long sponsorId,
            @PathVariable Long eventId) {
        try {
            boolean exists = sponsorEventService.checkExistingAssignment(sponsorId, eventId);
            return ResponseEntity.ok(exists);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/responsible/{responsibleUserId}/contracts")
    public ResponseEntity<List<ContractDTO>> getContractsByResponsibleUser(@PathVariable Long responsibleUserId) {
        try {
            List<ContractDTO> contracts = sponsorEventService.getContractsByResponsibleUser(responsibleUserId);
            return ResponseEntity.ok(contracts);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
  @PostMapping("/update-contract-status")
  public ResponseEntity<Void> updateContractStatus(
    @RequestParam("contractId") Long contractId,
    @RequestParam("status") String status,
    @RequestParam(value = "signature", required = false) String signature) {
    try {
      sponsorEventService.updateContractStatus(contractId, status, signature);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}

package tn.esprit.examen.Smartmeet.Services.GhanemRidene;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.Contract;
import tn.esprit.examen.Smartmeet.repositories.ContractRepository;


import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@RequiredArgsConstructor
@Service
public class SponsorStatsService {

  @Autowired
  private ContractRepository contractRepository;

  public List<Map<String, Object>> getAmountsBySponsor(Long eventId) {
    List<Contract> contracts = contractRepository.findByEventId(eventId);
    return contracts.stream()
      .collect(Collectors.groupingBy(
        contract -> contract.getSponsor().getNom(),
        Collectors.summingDouble(Contract::getAmount)
      ))
      .entrySet().stream()
      .map(entry -> Map.of("sponsorName", entry.getKey(), "amount", (Object) entry.getValue()))
      .toList();
  }

  public List<Map<String, Object>> getStatusDistribution(Long eventId) {
    List<Contract> contracts = contractRepository.findByEventId(eventId);
    return contracts.stream()
      .collect(Collectors.groupingBy(
        Contract::getStatus,
        Collectors.counting()
      ))
      .entrySet().stream()
      .map(entry -> Map.of("status", entry.getKey(), "count", (Object) entry.getValue()))
      .sorted((a, b) -> b.get("status").toString().compareTo(a.get("status").toString()))
      .toList();
  }

  public double getAverageSignatureTime(Long eventId) {
    List<Contract> contracts = contractRepository.findByEventId(eventId);
    return contracts.stream()
      .filter(contract -> contract.getSigningDate() != null)
      .mapToLong(contract -> ChronoUnit.DAYS.between(
        contract.getCreatedAt().toLocalDate(),
        contract.getSigningDate().toLocalDate()
      ))
      .average()
      .orElse(0.0);
  }
}

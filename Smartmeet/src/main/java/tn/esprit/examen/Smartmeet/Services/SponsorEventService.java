package tn.esprit.examen.Smartmeet.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.Contract;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.Sponsor;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Event;
import tn.esprit.examen.Smartmeet.entities.Users.TypeUserRole;
import tn.esprit.examen.Smartmeet.entities.Users.Users;
import tn.esprit.examen.Smartmeet.repositories.ContractRepository;
import tn.esprit.examen.Smartmeet.repositories.GhanemRiden.SponsorRepository;
import tn.esprit.examen.Smartmeet.repositories.SalmaBenRomdhan.IEventRepository;
import tn.esprit.examen.Smartmeet.repositories.Users.UserRepository;
import tn.esprit.examen.Smartmeet.dto.ContractDTO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SponsorEventService {

    @Autowired
    private SponsorRepository sponsorRepository;

    @Autowired
    private IEventRepository eventRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private UserRepository usersRepository;



    private final Path fileStorageLocation = Paths.get("uploads/contracts");

    public SponsorEventService() {
        try {
            Files.createDirectories(fileStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public List<Users> getUsersWithSponsorRole() {
        return usersRepository.findAll().stream()
                .filter(user -> user.getUserRole().contains(TypeUserRole.SPONSOR))
                .collect(Collectors.toList());
    }

    public Sponsor createSponsor(Sponsor sponsor, Long responsibleUserId) {
        Users responsibleUser = usersRepository.findById(responsibleUserId)
                .orElseThrow(() -> new RuntimeException("Responsible user not found"));

        if (!responsibleUser.getUserRole().contains(TypeUserRole.SPONSOR)) {
            throw new RuntimeException("Selected user does not have SPONSOR role");
        }

        sponsor.setResponsibleUser(responsibleUser);
        return sponsorRepository.save(sponsor);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Contract assignSponsorToEvent(Long sponsorId, Long eventId, MultipartFile contractFile,
                                       String terms, Double amount, LocalDateTime expiryDate) throws IOException {
        // Validate file type
        if (!contractFile.getContentType().equals("application/pdf")) {
            throw new IllegalArgumentException("Only PDF files are allowed");
        }

        Sponsor sponsor = sponsorRepository.findById(sponsorId)
                .orElseThrow(() -> new RuntimeException("Sponsor not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // Generate unique filename
        String originalFilename = contractFile.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = "contract_" + sponsorId + "_" + eventId + "_" + System.currentTimeMillis() + fileExtension;

        // Save the file
        Path targetLocation = fileStorageLocation.resolve(fileName);
        Files.copy(contractFile.getInputStream(), targetLocation);

        // Create and save contract
        Contract contract = new Contract();
        contract.setSponsor(sponsor);
        contract.setEvent(event);
        contract.setContractPath(targetLocation.toString());
        contract.setSigningDate(LocalDateTime.now());
        contract.setStatus("PENDING");
        contract.setTerms(terms);
        contract.setAmount(amount);
        contract.setExpiryDate(expiryDate);

        // Update sponsor with event
        sponsor.getEvents().add(event);
        sponsorRepository.save(sponsor);

        return contractRepository.save(contract);
    }

    public Contract simpleAssignSponsorToEvent(Long sponsorId, Long eventId,
                                             String terms, Double amount, LocalDateTime expiryDate) {
        Sponsor sponsor = sponsorRepository.findById(sponsorId)
                .orElseThrow(() -> new RuntimeException("Sponsor not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // Create and save contract
        Contract contract = new Contract();
        contract.setSponsor(sponsor);
        contract.setEvent(event);
        contract.setSigningDate(LocalDateTime.now());
        contract.setStatus("PENDING");
        contract.setTerms(terms);
        contract.setAmount(amount);
        contract.setExpiryDate(expiryDate);

        // Update sponsor with event
        sponsor.getEvents().add(event);
        sponsorRepository.save(sponsor);

        return contractRepository.save(contract);
    }

    public Set<Event> getSponsorEvents(Long sponsorId) {
        Sponsor sponsor = sponsorRepository.findById(sponsorId)
                .orElseThrow(() -> new RuntimeException("Sponsor not found"));
        return sponsor.getEvents();
    }

    public byte[] getContractFile(Long contractId) throws IOException {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Contract not found"));

        if (contract.getContractPath() == null) {
            throw new RuntimeException("No contract file found");
        }

        Path filePath = Paths.get(contract.getContractPath());
        return Files.readAllBytes(filePath);
    }

    public List<Contract> getSponsorContracts(Long sponsorId) {
        Sponsor sponsor = sponsorRepository.findById(sponsorId)
                .orElseThrow(() -> new RuntimeException("Sponsor not found"));
        return contractRepository.findBySponsor(sponsor);
    }

    public boolean checkExistingAssignment(Long sponsorId, Long eventId) {
        Sponsor sponsor = sponsorRepository.findById(sponsorId)
                .orElseThrow(() -> new RuntimeException("Sponsor not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        return sponsor.getEvents().contains(event);
    }

    public List<ContractDTO> getContractsByResponsibleUser(Long responsibleUserId) {
        Users responsibleUser = usersRepository.findById(responsibleUserId)
                .orElseThrow(() -> new RuntimeException("Responsible user not found"));

        List<Sponsor> sponsors = sponsorRepository.findByResponsibleUser(responsibleUser);
        return sponsors.stream()
                .flatMap(sponsor -> contractRepository.findBySponsor(sponsor).stream())
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ContractDTO convertToDTO(Contract contract) {
        ContractDTO dto = new ContractDTO();
        dto.setId(contract.getId());
        dto.setTerms(contract.getTerms());
        dto.setAmount(contract.getAmount());
        dto.setSigningDate(contract.getSigningDate().toString());
        dto.setExpiryDate(contract.getExpiryDate().toString());
        dto.setStatus(contract.getStatus());
        dto.setContractPath(contract.getContractPath());
        dto.setCreatedAt(contract.getCreatedAt().toString());
        dto.setUpdatedAt(contract.getUpdatedAt().toString());
        dto.setSignature(contract.getSignature());

        // Convert sponsor
        ContractDTO.SponsorDTO sponsorDTO = new ContractDTO.SponsorDTO();
        sponsorDTO.setIdSponsor(contract.getSponsor().getIdSponsor());
        sponsorDTO.setNom(contract.getSponsor().getNom());
        dto.setSponsor(sponsorDTO);

        // Convert event
        ContractDTO.EventDTO eventDTO = new ContractDTO.EventDTO();
        eventDTO.setId(contract.getEvent().getId());
        eventDTO.setTitle(contract.getEvent().getTitle());
        dto.setEvent(eventDTO);

        return dto;
    }


  public SponsorEventService(ContractRepository contractRepository) {
    this.contractRepository = contractRepository;
  }

  public void updateContractStatus(Long contractId, String status, String signature) {
    Contract contract = contractRepository.findById(contractId)
      .orElseThrow(() -> new IllegalArgumentException("Contract not found with ID: " + contractId));
    contract.setStatus(status);
    if (signature != null) {
      contract.setSignature(signature); // Sauvegarde la signature si elle est fournie
    }
    contractRepository.save(contract);
  }
}

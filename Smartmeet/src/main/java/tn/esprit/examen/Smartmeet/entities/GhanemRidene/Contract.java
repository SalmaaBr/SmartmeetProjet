package tn.esprit.examen.Smartmeet.entities.GhanemRidene;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan.Event;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
@JsonIgnore
    @ManyToOne
    @JoinColumn(name = "sponsor_id")
    @NotNull(message = "Sponsor is required")
    private Sponsor sponsor;
@JsonIgnore
    @ManyToOne
    @JoinColumn(name = "event_id")
    @NotNull(message = "Event is required")
    private Event event;

    @Column(name = "contract_path")
    @NotBlank(message = "Contract file is required")
    private String contractPath;

    @Column(name = "signing_date")
    @NotNull(message = "Signing date is required")
    private LocalDateTime signingDate;

    @Column(name = "status")
    @NotBlank(message = "Status is required")
    private String status; // PENDING, ACTIVE, EXPIRED, TERMINATED

    @Column(name = "terms", columnDefinition = "TEXT")
    @NotBlank(message = "Terms are required")
    private String terms;

    @Column(name = "amount")
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;

    @Column(name = "expiry_date")
    @NotNull(message = "Expiry date is required")
    private LocalDateTime expiryDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

  @Column(name = "signature", columnDefinition = "TEXT") // Champ pour stocker la signature en base64
  private String signature;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

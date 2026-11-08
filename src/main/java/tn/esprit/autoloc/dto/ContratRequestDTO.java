package tn.esprit.autoloc.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContratRequestDTO {

    @NotNull(message = "La date de signature du contrat est obligatoire")
    private LocalDate dateSignature;

    @NotNull(message = "Le montant total du contrat est obligatoire")
    @PositiveOrZero(message = "Le montant total du contrat ne peut pas être négatif")
    private BigDecimal montantTotal;

    @NotNull(message = "Le statut de validité du contrat est obligatoire")
    private Boolean valide;

    @NotNull(message = "La réservation associée au contrat est obligatoire")
    private Long reservationId;
}

package tn.esprit.autoloc.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.autoloc.domain.StatutReservation;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationWithContratRequestDTO {

    @NotNull(message = "La date de début de la réservation est obligatoire")
    @FutureOrPresent(message = "La date de début de la réservation doit être aujourd'hui ou dans le futur")
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin de la réservation est obligatoire")
    @FutureOrPresent(message = "La date de fin de la réservation doit être aujourd'hui ou dans le futur")
    private LocalDate dateFin;

    @NotNull(message = "Le statut de la réservation est obligatoire")
    private StatutReservation statut;

    @NotNull(message = "Le véhicule de la réservation est obligatoire")
    private Long vehiculeId;

    @NotNull(message = "Le client de la réservation est obligatoire")
    private Long clientId;

    @NotNull(message = "La date de signature du contrat est obligatoire")
    private LocalDate dateSignature;

    @NotNull(message = "Le montant total du contrat est obligatoire")
    @PositiveOrZero(message = "Le montant total du contrat ne peut pas être négatif")
    private BigDecimal montantTotal;

    @NotNull(message = "Le statut de validité du contrat est obligatoire")
    private Boolean valide;

    @AssertTrue(message = "La date de fin doit être postérieure ou égale à la date de début")
    public boolean isPeriodeValide() {
        if (dateDebut == null || dateFin == null) {
            return true;
        }
        return !dateFin.isBefore(dateDebut);
    }
}

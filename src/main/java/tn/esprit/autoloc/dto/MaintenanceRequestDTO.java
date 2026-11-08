package tn.esprit.autoloc.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
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
public class MaintenanceRequestDTO {

    @NotNull(message = "La date de début de la maintenance est obligatoire")
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin de la maintenance est obligatoire")
    private LocalDate dateFin;

    @NotBlank(message = "La description de la maintenance est obligatoire")
    @Size(max = 255, message = "La description de la maintenance ne doit pas dépasser 255 caractères")
    private String description;

    @NotNull(message = "Le coût de la maintenance est obligatoire")
    @PositiveOrZero(message = "Le coût de la maintenance ne peut pas être négatif")
    private BigDecimal cout;

    @NotNull(message = "Le véhicule concerné par la maintenance est obligatoire")
    private Long vehiculeId;

    @AssertTrue(message = "La date de fin doit être postérieure ou égale à la date de début")
    public boolean isPeriodeValide() {
        if (dateDebut == null || dateFin == null) {
            return true;
        }
        return !dateFin.isBefore(dateDebut);
    }
}

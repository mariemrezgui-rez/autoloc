package tn.esprit.autoloc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EquipementRequestDTO {

    @NotBlank(message = "Le libellé de l'équipement est obligatoire")
    @Size(max = 100, message = "Le libellé de l'équipement ne doit pas dépasser 100 caractères")
    private String libelle;

    @NotNull(message = "La quantité de l'équipement est obligatoire")
    @PositiveOrZero(message = "La quantité de l'équipement doit être supérieure ou égale à 0")
    private Integer quantite;
}

package tn.esprit.autoloc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.autoloc.domain.ModePaiement;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaiementResponseDTO {

    private Long idPaiement;

    private BigDecimal montant;

    private LocalDate datePaiement;

    private ModePaiement modePaiement;

    private Long contratId;
}

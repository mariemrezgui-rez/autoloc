package tn.esprit.autoloc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehiculeResponseDTO {

    private Long idVehicule;

    private String immatriculation;

    private String marque;

    private String modele;

    private CategorieVehicule categorie;

    private BigDecimal tarifJournalier;

    private StatutVehicule statut;

    private Long agenceId;

    private String agenceNom;

    private Set<Long> equipementIds;
}

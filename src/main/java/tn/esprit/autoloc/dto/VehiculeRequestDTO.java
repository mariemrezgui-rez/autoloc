package tn.esprit.autoloc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
public class VehiculeRequestDTO {

    @NotBlank(message = "L'immatriculation du véhicule est obligatoire")
    @Size(max = 20, message = "L'immatriculation du véhicule ne doit pas dépasser 20 caractères")
    private String immatriculation;

    @NotBlank(message = "La marque du véhicule est obligatoire")
    @Size(max = 50, message = "La marque du véhicule ne doit pas dépasser 50 caractères")
    private String marque;

    @NotBlank(message = "Le modèle du véhicule est obligatoire")
    @Size(max = 50, message = "Le modèle du véhicule ne doit pas dépasser 50 caractères")
    private String modele;

    @NotNull(message = "La catégorie du véhicule est obligatoire")
    private CategorieVehicule categorie;

    @NotNull(message = "Le tarif journalier du véhicule est obligatoire")
    @Positive(message = "Le tarif journalier du véhicule doit être strictement positif")
    private BigDecimal tarifJournalier;

    @NotNull(message = "Le statut du véhicule est obligatoire")
    private StatutVehicule statut;

    @NotNull(message = "L'agence du véhicule est obligatoire")
    private Long agenceId;

    private Set<Long> equipementIds;
}

package tn.esprit.autoloc.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.dto.VehiculeRequestDTO;
import tn.esprit.autoloc.dto.VehiculeResponseDTO;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class VehiculeMapper {

    public Vehicule toEntity(VehiculeRequestDTO dto) {
        Vehicule vehicule = new Vehicule();
        updateEntity(vehicule, dto);
        return vehicule;
    }

    public VehiculeResponseDTO toDTO(Vehicule vehicule) {
        VehiculeResponseDTO dto = new VehiculeResponseDTO();
        dto.setIdVehicule(vehicule.getIdVehicule());
        dto.setImmatriculation(vehicule.getImmatriculation());
        dto.setMarque(vehicule.getMarque());
        dto.setModele(vehicule.getModele());
        dto.setCategorie(vehicule.getCategorie());
        dto.setTarifJournalier(vehicule.getTarifJournalier());
        dto.setStatut(vehicule.getStatut());

        Agence agence = vehicule.getAgence();
        if (agence != null) {
            dto.setAgenceId(agence.getIdAgence());
            dto.setAgenceNom(agence.getNom());
        }

        if (vehicule.getEquipements() != null) {
            Set<Long> equipementIds = vehicule.getEquipements().stream()
                    .map(Equipement::getIdEquipement)
                    .collect(Collectors.toCollection(HashSet::new));
            dto.setEquipementIds(equipementIds);
        }
        return dto;
    }

    public void updateEntity(Vehicule vehicule, VehiculeRequestDTO dto) {
        vehicule.setImmatriculation(dto.getImmatriculation());
        vehicule.setMarque(dto.getMarque());
        vehicule.setModele(dto.getModele());
        vehicule.setCategorie(dto.getCategorie());
        vehicule.setTarifJournalier(dto.getTarifJournalier());
        vehicule.setStatut(dto.getStatut());
    }
}

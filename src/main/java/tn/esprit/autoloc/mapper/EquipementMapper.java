package tn.esprit.autoloc.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.dto.EquipementRequestDTO;
import tn.esprit.autoloc.dto.EquipementResponseDTO;

@Component
public class EquipementMapper {

    public Equipement toEntity(EquipementRequestDTO dto) {
        Equipement equipement = new Equipement();
        updateEntity(equipement, dto);
        return equipement;
    }

    public EquipementResponseDTO toDTO(Equipement equipement) {
        EquipementResponseDTO dto = new EquipementResponseDTO();
        dto.setIdEquipement(equipement.getIdEquipement());
        dto.setLibelle(equipement.getLibelle());
        dto.setQuantite(equipement.getQuantite());
        return dto;
    }

    public void updateEntity(Equipement equipement, EquipementRequestDTO dto) {
        equipement.setLibelle(dto.getLibelle());
        equipement.setQuantite(dto.getQuantite());
    }
}

package tn.esprit.autoloc.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.dto.EmployeRequestDTO;
import tn.esprit.autoloc.dto.EmployeResponseDTO;

@Component
public class EmployeMapper {

    public Employe toEntity(EmployeRequestDTO dto) {
        Employe employe = new Employe();
        updateEntity(employe, dto);
        return employe;
    }

    public EmployeResponseDTO toDTO(Employe employe) {
        EmployeResponseDTO dto = new EmployeResponseDTO();
        dto.setIdEmploye(employe.getIdEmploye());
        dto.setNom(employe.getNom());
        dto.setPrenom(employe.getPrenom());
        dto.setRole(employe.getRole());

        Agence agence = employe.getAgence();
        if (agence != null) {
            dto.setAgenceId(agence.getIdAgence());
            dto.setAgenceNom(agence.getNom());
        }
        return dto;
    }

    public void updateEntity(Employe employe, EmployeRequestDTO dto) {
        employe.setNom(dto.getNom());
        employe.setPrenom(dto.getPrenom());
        employe.setRole(dto.getRole());
    }
}

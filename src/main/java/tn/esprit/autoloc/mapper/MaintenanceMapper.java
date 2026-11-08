package tn.esprit.autoloc.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.dto.MaintenanceRequestDTO;
import tn.esprit.autoloc.dto.MaintenanceResponseDTO;

@Component
public class MaintenanceMapper {

    public Maintenance toEntity(MaintenanceRequestDTO dto) {
        Maintenance maintenance = new Maintenance();
        updateEntity(maintenance, dto);
        return maintenance;
    }

    public MaintenanceResponseDTO toDTO(Maintenance maintenance) {
        MaintenanceResponseDTO dto = new MaintenanceResponseDTO();
        dto.setIdMaintenance(maintenance.getIdMaintenance());
        dto.setDateDebut(maintenance.getDateDebut());
        dto.setDateFin(maintenance.getDateFin());
        dto.setDescription(maintenance.getDescription());
        dto.setCout(maintenance.getCout());

        Vehicule vehicule = maintenance.getVehicule();
        if (vehicule != null) {
            dto.setVehiculeId(vehicule.getIdVehicule());
            dto.setVehiculeImmatriculation(vehicule.getImmatriculation());
        }
        return dto;
    }

    public void updateEntity(Maintenance maintenance, MaintenanceRequestDTO dto) {
        maintenance.setDateDebut(dto.getDateDebut());
        maintenance.setDateFin(dto.getDateFin());
        maintenance.setDescription(dto.getDescription());
        maintenance.setCout(dto.getCout());
    }
}

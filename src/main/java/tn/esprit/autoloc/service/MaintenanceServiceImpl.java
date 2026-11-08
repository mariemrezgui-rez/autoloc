package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.dto.MaintenanceRequestDTO;
import tn.esprit.autoloc.dto.MaintenanceResponseDTO;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.mapper.MaintenanceMapper;
import tn.esprit.autoloc.repository.IMaintenanceRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MaintenanceServiceImpl implements IMaintenanceService {

    private final IMaintenanceRepository maintenanceRepository;
    private final IVehiculeRepository vehiculeRepository;
    private final MaintenanceMapper maintenanceMapper;

    @Override
    public MaintenanceResponseDTO create(MaintenanceRequestDTO dto) {
        Maintenance maintenance = maintenanceMapper.toEntity(dto);
        maintenance.setVehicule(chargerVehicule(dto.getVehiculeId()));
        return maintenanceMapper.toDTO(maintenanceRepository.save(maintenance));
    }

    @Override
    @Transactional(readOnly = true)
    public MaintenanceResponseDTO findById(Long id) {
        return maintenanceMapper.toDTO(chargerMaintenance(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceResponseDTO> findAll() {
        return maintenanceRepository.findAll().stream()
                .map(maintenanceMapper::toDTO)
                .toList();
    }

    @Override
    public MaintenanceResponseDTO update(Long id, MaintenanceRequestDTO dto) {
        Maintenance existante = chargerMaintenance(id);
        maintenanceMapper.updateEntity(existante, dto);
        existante.setVehicule(chargerVehicule(dto.getVehiculeId()));
        return maintenanceMapper.toDTO(maintenanceRepository.save(existante));
    }

    @Override
    public void deleteById(Long id) {
        if (!maintenanceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Maintenance", id);
        }
        maintenanceRepository.deleteById(id);
    }

    private Maintenance chargerMaintenance(Long id) {
        return maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance", id));
    }

    private Vehicule chargerVehicule(Long id) {
        return vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", id));
    }
}

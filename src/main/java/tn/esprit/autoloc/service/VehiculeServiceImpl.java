package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.dto.VehiculeRequestDTO;
import tn.esprit.autoloc.dto.VehiculeResponseDTO;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.mapper.VehiculeMapper;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.repository.IEquipementRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class VehiculeServiceImpl implements IVehiculeService {

    private final IVehiculeRepository vehiculeRepository;
    private final IAgenceRepository agenceRepository;
    private final IEquipementRepository equipementRepository;
    private final VehiculeMapper vehiculeMapper;

    @Override
    public VehiculeResponseDTO create(VehiculeRequestDTO dto) {
        Vehicule vehicule = vehiculeMapper.toEntity(dto);
        vehicule.setAgence(chargerAgence(dto.getAgenceId()));
        vehicule.setEquipements(chargerEquipements(dto.getEquipementIds()));
        return vehiculeMapper.toDTO(vehiculeRepository.save(vehicule));
    }

    @Override
    @Transactional(readOnly = true)
    public VehiculeResponseDTO findById(Long id) {
        return vehiculeMapper.toDTO(chargerVehicule(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculeResponseDTO> findAll() {
        return vehiculeRepository.findAll().stream()
                .map(vehiculeMapper::toDTO)
                .toList();
    }

    @Override
    public VehiculeResponseDTO update(Long id, VehiculeRequestDTO dto) {
        Vehicule existante = chargerVehicule(id);
        vehiculeMapper.updateEntity(existante, dto);
        existante.setAgence(chargerAgence(dto.getAgenceId()));
        existante.getEquipements().clear();
        existante.getEquipements().addAll(chargerEquipements(dto.getEquipementIds()));
        return vehiculeMapper.toDTO(vehiculeRepository.save(existante));
    }

    @Override
    public void deleteById(Long id) {
        if (!vehiculeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vehicule", id);
        }
        vehiculeRepository.deleteById(id);
    }

    private Vehicule chargerVehicule(Long id) {
        return vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", id));
    }

    private Agence chargerAgence(Long id) {
        return agenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agence", id));
    }

    private Set<Equipement> chargerEquipements(Set<Long> equipementIds) {
        Set<Equipement> equipements = new HashSet<>();
        if (equipementIds == null || equipementIds.isEmpty()) {
            return equipements;
        }
        for (Long idEquipement : equipementIds) {
            equipements.add(equipementRepository.findById(idEquipement)
                    .orElseThrow(() -> new ResourceNotFoundException("Equipement", idEquipement)));
        }
        return equipements;
    }
}

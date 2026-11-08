package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.dto.EmployeRequestDTO;
import tn.esprit.autoloc.dto.EmployeResponseDTO;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.mapper.EmployeMapper;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.repository.IEmployeRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeServiceImpl implements IEmployeService {

    private final IEmployeRepository employeRepository;
    private final IAgenceRepository agenceRepository;
    private final EmployeMapper employeMapper;

    @Override
    public EmployeResponseDTO create(EmployeRequestDTO dto) {
        Employe employe = employeMapper.toEntity(dto);
        employe.setAgence(chargerAgence(dto.getAgenceId()));
        return employeMapper.toDTO(employeRepository.save(employe));
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeResponseDTO findById(Long id) {
        return employeMapper.toDTO(chargerEmploye(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeResponseDTO> findAll() {
        return employeRepository.findAll().stream()
                .map(employeMapper::toDTO)
                .toList();
    }

    @Override
    public EmployeResponseDTO update(Long id, EmployeRequestDTO dto) {
        Employe existante = chargerEmploye(id);
        employeMapper.updateEntity(existante, dto);
        existante.setAgence(chargerAgence(dto.getAgenceId()));
        return employeMapper.toDTO(employeRepository.save(existante));
    }

    @Override
    public void deleteById(Long id) {
        if (!employeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employe", id);
        }
        employeRepository.deleteById(id);
    }

    private Employe chargerEmploye(Long id) {
        return employeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employe", id));
    }

    private Agence chargerAgence(Long id) {
        return agenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agence", id));
    }
}

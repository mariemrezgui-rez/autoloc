package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.dto.AgenceRequestDTO;
import tn.esprit.autoloc.dto.AgenceResponseDTO;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.mapper.AgenceMapper;
import tn.esprit.autoloc.repository.IAgenceRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AgenceServiceImpl implements IAgenceService {

    private final IAgenceRepository agenceRepository;
    private final AgenceMapper agenceMapper;

    @Override
    public AgenceResponseDTO create(AgenceRequestDTO dto) {
        Agence agence = agenceMapper.toEntity(dto);
        return agenceMapper.toDTO(agenceRepository.save(agence));
    }

    @Override
    @Transactional(readOnly = true)
    public AgenceResponseDTO findById(Long id) {
        return agenceMapper.toDTO(chargerAgence(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgenceResponseDTO> findAll() {
        return agenceRepository.findAll().stream()
                .map(agenceMapper::toDTO)
                .toList();
    }

    @Override
    public AgenceResponseDTO update(Long id, AgenceRequestDTO dto) {
        Agence existante = chargerAgence(id);
        agenceMapper.updateEntity(existante, dto);
        return agenceMapper.toDTO(agenceRepository.save(existante));
    }

    @Override
    public void deleteById(Long id) {
        if (!agenceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Agence", id);
        }
        agenceRepository.deleteById(id);
    }

    private Agence chargerAgence(Long id) {
        return agenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agence", id));
    }
}

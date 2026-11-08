package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.dto.PaiementRequestDTO;
import tn.esprit.autoloc.dto.PaiementResponseDTO;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.mapper.PaiementMapper;
import tn.esprit.autoloc.repository.IContratRepository;
import tn.esprit.autoloc.repository.IPaiementRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PaiementServiceImpl implements IPaiementService {

    private final IPaiementRepository paiementRepository;
    private final IContratRepository contratRepository;
    private final PaiementMapper paiementMapper;

    @Override
    public PaiementResponseDTO create(PaiementRequestDTO dto) {
        Paiement paiement = paiementMapper.toEntity(dto);
        paiement.setContrat(chargerContrat(dto.getContratId()));
        return paiementMapper.toDTO(paiementRepository.save(paiement));
    }

    @Override
    @Transactional(readOnly = true)
    public PaiementResponseDTO findById(Long id) {
        return paiementMapper.toDTO(chargerPaiement(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaiementResponseDTO> findAll() {
        return paiementRepository.findAll().stream()
                .map(paiementMapper::toDTO)
                .toList();
    }

    @Override
    public PaiementResponseDTO update(Long id, PaiementRequestDTO dto) {
        Paiement existante = chargerPaiement(id);
        paiementMapper.updateEntity(existante, dto);
        existante.setContrat(chargerContrat(dto.getContratId()));
        return paiementMapper.toDTO(paiementRepository.save(existante));
    }

    @Override
    public void deleteById(Long id) {
        if (!paiementRepository.existsById(id)) {
            throw new ResourceNotFoundException("Paiement", id);
        }
        paiementRepository.deleteById(id);
    }

    private Paiement chargerPaiement(Long id) {
        return paiementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement", id));
    }

    private Contrat chargerContrat(Long id) {
        return contratRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat", id));
    }
}

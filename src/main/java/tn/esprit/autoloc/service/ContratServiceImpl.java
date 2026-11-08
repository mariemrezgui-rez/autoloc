package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.dto.ContratRequestDTO;
import tn.esprit.autoloc.dto.ContratResponseDTO;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.mapper.ContratMapper;
import tn.esprit.autoloc.repository.IContratRepository;
import tn.esprit.autoloc.repository.IReservationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ContratServiceImpl implements IContratService {

    private final IContratRepository contratRepository;
    private final IReservationRepository reservationRepository;
    private final ContratMapper contratMapper;

    @Override
    public ContratResponseDTO create(ContratRequestDTO dto) {
        Contrat contrat = contratMapper.toEntity(dto);
        contrat.setReservation(chargerReservation(dto.getReservationId()));
        return contratMapper.toDTO(contratRepository.save(contrat));
    }

    @Override
    @Transactional(readOnly = true)
    public ContratResponseDTO findById(Long id) {
        return contratMapper.toDTO(chargerContrat(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContratResponseDTO> findAll() {
        return contratRepository.findAll().stream()
                .map(contratMapper::toDTO)
                .toList();
    }

    @Override
    public ContratResponseDTO update(Long id, ContratRequestDTO dto) {
        Contrat existante = chargerContrat(id);
        contratMapper.updateEntity(existante, dto);
        existante.setReservation(chargerReservation(dto.getReservationId()));
        return contratMapper.toDTO(contratRepository.save(existante));
    }

    @Override
    public void deleteById(Long id) {
        if (!contratRepository.existsById(id)) {
            throw new ResourceNotFoundException("Contrat", id);
        }
        contratRepository.deleteById(id);
    }

    private Contrat chargerContrat(Long id) {
        return contratRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat", id));
    }

    private Reservation chargerReservation(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", id));
    }
}

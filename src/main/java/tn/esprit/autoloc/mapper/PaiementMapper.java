package tn.esprit.autoloc.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.dto.PaiementRequestDTO;
import tn.esprit.autoloc.dto.PaiementResponseDTO;

@Component
public class PaiementMapper {

    public Paiement toEntity(PaiementRequestDTO dto) {
        Paiement paiement = new Paiement();
        updateEntity(paiement, dto);
        return paiement;
    }

    public PaiementResponseDTO toDTO(Paiement paiement) {
        PaiementResponseDTO dto = new PaiementResponseDTO();
        dto.setIdPaiement(paiement.getIdPaiement());
        dto.setMontant(paiement.getMontant());
        dto.setDatePaiement(paiement.getDatePaiement());
        dto.setModePaiement(paiement.getModePaiement());

        Contrat contrat = paiement.getContrat();
        if (contrat != null) {
            dto.setContratId(contrat.getIdContrat());
        }
        return dto;
    }

    public void updateEntity(Paiement paiement, PaiementRequestDTO dto) {
        paiement.setMontant(dto.getMontant());
        paiement.setDatePaiement(dto.getDatePaiement());
        paiement.setModePaiement(dto.getModePaiement());
    }
}

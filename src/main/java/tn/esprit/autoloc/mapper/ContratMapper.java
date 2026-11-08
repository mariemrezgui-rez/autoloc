package tn.esprit.autoloc.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.dto.ContratRequestDTO;
import tn.esprit.autoloc.dto.ContratResponseDTO;

@Component
public class ContratMapper {

    public Contrat toEntity(ContratRequestDTO dto) {
        Contrat contrat = new Contrat();
        updateEntity(contrat, dto);
        return contrat;
    }

    public ContratResponseDTO toDTO(Contrat contrat) {
        ContratResponseDTO dto = new ContratResponseDTO();
        dto.setIdContrat(contrat.getIdContrat());
        dto.setDateSignature(contrat.getDateSignature());
        dto.setMontantTotal(contrat.getMontantTotal());
        dto.setValide(contrat.getValide());

        Reservation reservation = contrat.getReservation();
        if (reservation != null) {
            dto.setReservationId(reservation.getIdReservation());
            dto.setReservationDateDebut(reservation.getDateDebut());
            dto.setReservationDateFin(reservation.getDateFin());
            dto.setReservationStatut(reservation.getStatut());
        }
        return dto;
    }

    public void updateEntity(Contrat contrat, ContratRequestDTO dto) {
        contrat.setDateSignature(dto.getDateSignature());
        contrat.setMontantTotal(dto.getMontantTotal());
        contrat.setValide(dto.getValide());
    }
}

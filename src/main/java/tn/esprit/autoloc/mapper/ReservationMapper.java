package tn.esprit.autoloc.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.dto.ReservationRequestDTO;
import tn.esprit.autoloc.dto.ReservationResponseDTO;

@Component
public class ReservationMapper {

    public Reservation toEntity(ReservationRequestDTO dto) {
        Reservation reservation = new Reservation();
        updateEntity(reservation, dto);
        return reservation;
    }

    public ReservationResponseDTO toDTO(Reservation reservation) {
        ReservationResponseDTO dto = new ReservationResponseDTO();
        dto.setIdReservation(reservation.getIdReservation());
        dto.setDateDebut(reservation.getDateDebut());
        dto.setDateFin(reservation.getDateFin());
        dto.setStatut(reservation.getStatut());

        Vehicule vehicule = reservation.getVehicule();
        if (vehicule != null) {
            dto.setVehiculeId(vehicule.getIdVehicule());
            dto.setVehiculeImmatriculation(vehicule.getImmatriculation());
        }

        Client client = reservation.getClient();
        if (client != null) {
            dto.setClientId(client.getIdClient());
            dto.setClientNom(client.getNom());
            dto.setClientPrenom(client.getPrenom());
        }
        return dto;
    }

    public void updateEntity(Reservation reservation, ReservationRequestDTO dto) {
        reservation.setDateDebut(dto.getDateDebut());
        reservation.setDateFin(dto.getDateFin());
        reservation.setStatut(dto.getStatut());
    }
}

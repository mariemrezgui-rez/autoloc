package tn.esprit.autoloc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.autoloc.domain.StatutReservation;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponseDTO {

    private Long idReservation;

    private LocalDate dateDebut;

    private LocalDate dateFin;

    private StatutReservation statut;

    private Long vehiculeId;

    private String vehiculeImmatriculation;

    private Long clientId;

    private String clientNom;

    private String clientPrenom;
}

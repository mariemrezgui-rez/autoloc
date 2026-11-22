package tn.esprit.autoloc.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.autoloc.dto.ReservationResponseDTO;
import tn.esprit.autoloc.dto.ReservationWithContratRequestDTO;
import tn.esprit.autoloc.service.IReservationService;

@RestController
@RequestMapping("/api/reservations-contrats")
@RequiredArgsConstructor
public class ReservationContratController {

    private final IReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponseDTO> creerReservationEtContratAssocie(
            @Valid @RequestBody ReservationWithContratRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservationService.creerReservationEtContratAssocie(dto));
    }
}

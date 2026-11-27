package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IReservationRepository;

import java.time.Clock;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class VehiculeSchedulerService {

    private static final StatutReservation STATUT_LOCATION_ACTIVE = StatutReservation.CONFIRMEE;

    private final IReservationRepository reservationRepository;
    private final Clock clock;

    @Scheduled(cron = "0 0 1 * * *", zone = "Africa/Tunis")
    @Transactional
    public void cloturerLocationsTerminees() {
        LocalDate aujourdHui = LocalDate.now(clock);
        List<Reservation> reservations = reservationRepository
                .findLocationsTermineesNonCloturees(STATUT_LOCATION_ACTIVE, aujourdHui);

        if (reservations.isEmpty()) {
            log.info("Aucune location terminée à clôturer au {}", aujourdHui);
            return;
        }

        Set<Long> vehiculesOccupes = new HashSet<>(
                reservationRepository.findVehiculeIdsAvecLocationEnCours(STATUT_LOCATION_ACTIVE, aujourdHui));
        int locationsCloturees = 0;
        int vehiculesLiberes = 0;

        for (Reservation reservation : reservations) {
            reservation.setStatut(StatutReservation.TERMINEE);
            locationsCloturees++;

            Vehicule vehicule = reservation.getVehicule();
            if (vehicule == null) {
                log.warn("Réservation {} clôturée sans véhicule associé", reservation.getIdReservation());
                continue;
            }
            if (vehicule.getStatut() == StatutVehicule.MAINTENANCE
                    || vehiculesOccupes.contains(vehicule.getIdVehicule())) {
                continue;
            }

            vehicule.setStatut(StatutVehicule.DISPONIBLE);
            vehiculesLiberes++;
        }

        log.info("{} location(s) clôturée(s), {} véhicule(s) libéré(s) au {}",
                locationsCloturees, vehiculesLiberes, aujourdHui);
    }
}

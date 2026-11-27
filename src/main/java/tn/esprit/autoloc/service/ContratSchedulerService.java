package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.repository.IContratRepository;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContratSchedulerService {

    private final IContratRepository contratRepository;
    private final Clock clock;

    @Scheduled(cron = "0 0 7 * * MON-FRI", zone = "Africa/Tunis")
    @Transactional(readOnly = true)
    public void notifierContratsArrivantAEcheance() {
        LocalDate aujourdHui = LocalDate.now(clock);
        LocalDate finPeriode = aujourdHui.plusDays(2);
        List<Contrat> contrats = contratRepository
                .findByReservationDateFinBetweenAndReservationStatutNotIn(
                        aujourdHui, finPeriode,
                        List.of(StatutReservation.ANNULEE, StatutReservation.TERMINEE));

        if (contrats.isEmpty()) {
            log.info("Aucun contrat arrivant à échéance entre {} et {}", aujourdHui, finPeriode);
            return;
        }

        log.info("{} contrat(s) arrivant à échéance entre {} et {}", contrats.size(), aujourdHui, finPeriode);
        for (Contrat contrat : contrats) {
            log.info("Contrat {} : date de fin {}", contrat.getIdContrat(), contrat.getReservation().getDateFin());
        }
    }
}

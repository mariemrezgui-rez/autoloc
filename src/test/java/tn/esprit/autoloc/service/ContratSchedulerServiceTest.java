package tn.esprit.autoloc.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.repository.IContratRepository;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContratSchedulerServiceTest {

    private static final LocalDate AUJOURD_HUI = LocalDate.of(2026, 6, 15);
    private static final List<StatutReservation> STATUTS_EXCLUS =
            List.of(StatutReservation.ANNULEE, StatutReservation.TERMINEE);

    @Mock
    private IContratRepository contratRepository;

    private ContratSchedulerService service;

    @BeforeEach
    void setUp() {
        ZoneId zone = ZoneId.of("Africa/Tunis");
        service = new ContratSchedulerService(contratRepository,
                Clock.fixed(AUJOURD_HUI.atStartOfDay(zone).toInstant(), zone));
    }

    @Test
    void notifieLesContratsDeLaFenetreDe48Heures() {
        Reservation reservationDebut = new Reservation();
        reservationDebut.setIdReservation(101L);
        reservationDebut.setDateFin(AUJOURD_HUI);
        Contrat contratDebut = new Contrat();
        contratDebut.setIdContrat(1L);
        contratDebut.setReservation(reservationDebut);

        Reservation reservationFin = new Reservation();
        reservationFin.setIdReservation(102L);
        reservationFin.setDateFin(AUJOURD_HUI.plusDays(2));
        Contrat contratFin = new Contrat();
        contratFin.setIdContrat(2L);
        contratFin.setReservation(reservationFin);

        when(contratRepository.findByReservationDateFinBetweenAndReservationStatutNotIn(
                AUJOURD_HUI, AUJOURD_HUI.plusDays(2), STATUTS_EXCLUS))
                .thenReturn(List.of(contratDebut, contratFin));

        assertThatCode(service::notifierContratsArrivantAEcheance).doesNotThrowAnyException();

        verify(contratRepository).findByReservationDateFinBetweenAndReservationStatutNotIn(
                AUJOURD_HUI, AUJOURD_HUI.plusDays(2), STATUTS_EXCLUS);
        verifyNoMoreInteractions(contratRepository);
    }

    @Test
    void neEchouePasSansContrat() {
        when(contratRepository.findByReservationDateFinBetweenAndReservationStatutNotIn(
                AUJOURD_HUI, AUJOURD_HUI.plusDays(2), STATUTS_EXCLUS))
                .thenReturn(List.of());

        assertThatCode(service::notifierContratsArrivantAEcheance).doesNotThrowAnyException();

        verify(contratRepository).findByReservationDateFinBetweenAndReservationStatutNotIn(
                AUJOURD_HUI, AUJOURD_HUI.plusDays(2), STATUTS_EXCLUS);
        verifyNoMoreInteractions(contratRepository);
    }
}

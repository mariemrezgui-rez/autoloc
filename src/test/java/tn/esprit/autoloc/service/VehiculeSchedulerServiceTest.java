package tn.esprit.autoloc.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IReservationRepository;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehiculeSchedulerServiceTest {

    private static final LocalDate AUJOURD_HUI = LocalDate.of(2026, 6, 15);

    @Mock
    private IReservationRepository reservationRepository;

    private VehiculeSchedulerService service;

    @BeforeEach
    void setUp() {
        ZoneId zone = ZoneId.of("Africa/Tunis");
        service = new VehiculeSchedulerService(reservationRepository,
                Clock.fixed(AUJOURD_HUI.atStartOfDay(zone).toInstant(), zone));
    }

    @Test
    void clotureUneLocationEnRetardEtLibereLeVehicule() {
        Reservation reservation = reservation(10L, StatutVehicule.LOUE);
        when(reservationRepository.findLocationsTermineesNonCloturees(
                StatutReservation.CONFIRMEE, AUJOURD_HUI)).thenReturn(List.of(reservation));
        when(reservationRepository.findVehiculeIdsAvecLocationEnCours(
                StatutReservation.CONFIRMEE, AUJOURD_HUI)).thenReturn(List.of());

        assertThatCode(service::cloturerLocationsTerminees).doesNotThrowAnyException();

        assertThat(reservation.getStatut()).isEqualTo(StatutReservation.TERMINEE);
        assertThat(reservation.getVehicule().getStatut()).isEqualTo(StatutVehicule.DISPONIBLE);
        verify(reservationRepository).findLocationsTermineesNonCloturees(
                StatutReservation.CONFIRMEE, AUJOURD_HUI);
        verify(reservationRepository).findVehiculeIdsAvecLocationEnCours(
                StatutReservation.CONFIRMEE, AUJOURD_HUI);
        verifyNoMoreInteractions(reservationRepository);
    }

    @Test
    void neCloturePasUneLocationQuiFinitAujourdhui() {
        when(reservationRepository.findLocationsTermineesNonCloturees(
                StatutReservation.CONFIRMEE, AUJOURD_HUI)).thenReturn(List.of());

        assertThatCode(service::cloturerLocationsTerminees).doesNotThrowAnyException();

        verify(reservationRepository).findLocationsTermineesNonCloturees(
                StatutReservation.CONFIRMEE, AUJOURD_HUI);
        verifyNoMoreInteractions(reservationRepository);
    }

    @Test
    void clotureSansLibererUnVehiculeEnMaintenance() {
        Reservation reservation = reservation(11L, StatutVehicule.MAINTENANCE);
        when(reservationRepository.findLocationsTermineesNonCloturees(
                StatutReservation.CONFIRMEE, AUJOURD_HUI)).thenReturn(List.of(reservation));
        when(reservationRepository.findVehiculeIdsAvecLocationEnCours(
                StatutReservation.CONFIRMEE, AUJOURD_HUI)).thenReturn(List.of());

        service.cloturerLocationsTerminees();

        assertThat(reservation.getStatut()).isEqualTo(StatutReservation.TERMINEE);
        assertThat(reservation.getVehicule().getStatut()).isEqualTo(StatutVehicule.MAINTENANCE);
    }

    @Test
    void clotureSansLibererUnVehiculeAvecUneAutreLocationEnCours() {
        Reservation reservation = reservation(12L, StatutVehicule.LOUE);
        when(reservationRepository.findLocationsTermineesNonCloturees(
                StatutReservation.CONFIRMEE, AUJOURD_HUI)).thenReturn(List.of(reservation));
        when(reservationRepository.findVehiculeIdsAvecLocationEnCours(
                StatutReservation.CONFIRMEE, AUJOURD_HUI)).thenReturn(List.of(12L));

        service.cloturerLocationsTerminees();

        assertThat(reservation.getStatut()).isEqualTo(StatutReservation.TERMINEE);
        assertThat(reservation.getVehicule().getStatut()).isEqualTo(StatutVehicule.LOUE);
    }

    @Test
    void clotureUneReservationSansVehiculeSansEchouerLeTraitement() {
        Reservation reservation = reservation(13L, StatutVehicule.LOUE);
        reservation.setVehicule(null);
        when(reservationRepository.findLocationsTermineesNonCloturees(
                StatutReservation.CONFIRMEE, AUJOURD_HUI)).thenReturn(List.of(reservation));
        when(reservationRepository.findVehiculeIdsAvecLocationEnCours(
                StatutReservation.CONFIRMEE, AUJOURD_HUI)).thenReturn(List.of());

        assertThatCode(service::cloturerLocationsTerminees).doesNotThrowAnyException();

        assertThat(reservation.getStatut()).isEqualTo(StatutReservation.TERMINEE);
    }

    @Test
    void deuxiemeExecutionNeModifieRien() {
        Reservation reservation = reservation(14L, StatutVehicule.LOUE);
        when(reservationRepository.findLocationsTermineesNonCloturees(
                StatutReservation.CONFIRMEE, AUJOURD_HUI)).thenReturn(List.of(reservation), List.of());
        when(reservationRepository.findVehiculeIdsAvecLocationEnCours(
                StatutReservation.CONFIRMEE, AUJOURD_HUI)).thenReturn(List.of());

        service.cloturerLocationsTerminees();
        service.cloturerLocationsTerminees();

        assertThat(reservation.getStatut()).isEqualTo(StatutReservation.TERMINEE);
        assertThat(reservation.getVehicule().getStatut()).isEqualTo(StatutVehicule.DISPONIBLE);
        verify(reservationRepository, times(2)).findLocationsTermineesNonCloturees(
                StatutReservation.CONFIRMEE, AUJOURD_HUI);
        verify(reservationRepository).findVehiculeIdsAvecLocationEnCours(
                StatutReservation.CONFIRMEE, AUJOURD_HUI);
    }

    private Reservation reservation(Long id, StatutVehicule statutVehicule) {
        Vehicule vehicule = new Vehicule();
        vehicule.setIdVehicule(id);
        vehicule.setStatut(statutVehicule);

        Reservation reservation = new Reservation();
        reservation.setIdReservation(id);
        reservation.setStatut(StatutReservation.CONFIRMEE);
        reservation.setVehicule(vehicule);
        return reservation;
    }
}

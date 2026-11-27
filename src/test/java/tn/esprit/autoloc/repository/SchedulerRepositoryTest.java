package tn.esprit.autoloc.repository;

import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class SchedulerRepositoryTest {

    private static final LocalDate AUJOURD_HUI = LocalDate.of(2026, 6, 15);

    @Autowired
    private IContratRepository contratRepository;

    @Autowired
    private IReservationRepository reservationRepository;

    @Autowired
    private IVehiculeRepository vehiculeRepository;

    @Test
    void contratsEcheanceBornesInclusesEtStatutsExclus() {
        Contrat debut = creerContrat(AUJOURD_HUI, StatutReservation.CONFIRMEE);
        Contrat fin = creerContrat(AUJOURD_HUI.plusDays(2), StatutReservation.CONFIRMEE);
        creerContrat(AUJOURD_HUI.plusDays(3), StatutReservation.CONFIRMEE);
        creerContrat(AUJOURD_HUI.minusDays(1), StatutReservation.CONFIRMEE);
        creerContrat(AUJOURD_HUI, StatutReservation.ANNULEE);
        creerContrat(AUJOURD_HUI, StatutReservation.TERMINEE);

        List<Contrat> contrats = contratRepository.findByReservationDateFinBetweenAndReservationStatutNotIn(
                AUJOURD_HUI, AUJOURD_HUI.plusDays(2),
                List.of(StatutReservation.ANNULEE, StatutReservation.TERMINEE));

        assertThat(contrats).extracting(Contrat::getIdContrat)
                .containsExactlyInAnyOrder(debut.getIdContrat(), fin.getIdContrat());
        assertThat(contrats).allSatisfy(contrat ->
                assertThat(Hibernate.isInitialized(contrat.getReservation())).isTrue());
    }

    @Test
    void locationsTermineesUniquementConfirmeesAvecDateFinStrictementAnterieure() {
        Vehicule vehicule = creerVehicule("AA-123-AA");
        Reservation hier = creerReservation(
                vehicule, AUJOURD_HUI.minusDays(3), AUJOURD_HUI.minusDays(1), StatutReservation.CONFIRMEE);
        Reservation ancienne = creerReservation(
                vehicule, AUJOURD_HUI.minusDays(10), AUJOURD_HUI.minusDays(5), StatutReservation.CONFIRMEE);
        Reservation sansVehicule = creerReservation(
                null, AUJOURD_HUI.minusDays(2), AUJOURD_HUI.minusDays(1), StatutReservation.CONFIRMEE);
        Reservation aujourdHui = creerReservation(
                vehicule, AUJOURD_HUI.minusDays(1), AUJOURD_HUI, StatutReservation.CONFIRMEE);
        Reservation annulee = creerReservation(
                vehicule, AUJOURD_HUI.minusDays(2), AUJOURD_HUI.minusDays(1), StatutReservation.ANNULEE);
        Reservation enAttente = creerReservation(
                vehicule, AUJOURD_HUI.minusDays(2), AUJOURD_HUI.minusDays(1), StatutReservation.EN_ATTENTE);
        Reservation terminee = creerReservation(
                vehicule, AUJOURD_HUI.minusDays(2), AUJOURD_HUI.minusDays(1), StatutReservation.TERMINEE);

        List<Reservation> reservations = reservationRepository.findLocationsTermineesNonCloturees(
                StatutReservation.CONFIRMEE, AUJOURD_HUI);

        assertThat(reservations).extracting(Reservation::getIdReservation).containsExactlyInAnyOrder(
                hier.getIdReservation(), ancienne.getIdReservation(), sansVehicule.getIdReservation());
        assertThat(reservations).extracting(Reservation::getIdReservation).doesNotContain(
                aujourdHui.getIdReservation(), annulee.getIdReservation(),
                enAttente.getIdReservation(), terminee.getIdReservation());
    }

    @Test
    void vehiculesAvecLocationEnCoursUniquementConfirmeesEtSansDoublon() {
        Vehicule occupe = creerVehicule("BB-123-BB");
        creerReservation(occupe, AUJOURD_HUI.minusDays(1), AUJOURD_HUI.plusDays(1), StatutReservation.CONFIRMEE);
        creerReservation(occupe, AUJOURD_HUI, AUJOURD_HUI.plusDays(2), StatutReservation.CONFIRMEE);

        Vehicule futur = creerVehicule("CC-123-CC");
        creerReservation(futur, AUJOURD_HUI.plusDays(1), AUJOURD_HUI.plusDays(3), StatutReservation.CONFIRMEE);

        Vehicule termine = creerVehicule("DD-123-DD");
        creerReservation(termine, AUJOURD_HUI.minusDays(5), AUJOURD_HUI.minusDays(1), StatutReservation.CONFIRMEE);

        Vehicule annule = creerVehicule("EE-123-EE");
        creerReservation(annule, AUJOURD_HUI.minusDays(1), AUJOURD_HUI.plusDays(1), StatutReservation.ANNULEE);

        List<Long> ids = reservationRepository.findVehiculeIdsAvecLocationEnCours(
                StatutReservation.CONFIRMEE, AUJOURD_HUI);

        assertThat(ids).containsExactly(occupe.getIdVehicule());
    }

    private Contrat creerContrat(LocalDate dateFin, StatutReservation statut) {
        Reservation reservation = new Reservation();
        reservation.setDateDebut(dateFin.minusDays(1));
        reservation.setDateFin(dateFin);
        reservation.setStatut(statut);
        Reservation sauvegardee = reservationRepository.save(reservation);

        Contrat contrat = new Contrat();
        contrat.setDateSignature(AUJOURD_HUI.minusDays(1));
        contrat.setValide(true);
        contrat.setReservation(sauvegardee);
        return contratRepository.save(contrat);
    }

    private Vehicule creerVehicule(String immatriculation) {
        Vehicule vehicule = new Vehicule();
        vehicule.setImmatriculation(immatriculation);
        vehicule.setMarque("Peugeot");
        vehicule.setModele("208");
        vehicule.setCategorie(CategorieVehicule.CITADINE);
        vehicule.setTarifJournalier(BigDecimal.TEN);
        vehicule.setStatut(StatutVehicule.LOUE);
        return vehiculeRepository.save(vehicule);
    }

    private Reservation creerReservation(Vehicule vehicule, LocalDate dateDebut, LocalDate dateFin,
                                         StatutReservation statut) {
        Reservation reservation = new Reservation();
        reservation.setVehicule(vehicule);
        reservation.setDateDebut(dateDebut);
        reservation.setDateFin(dateFin);
        reservation.setStatut(statut);
        return reservationRepository.save(reservation);
    }
}

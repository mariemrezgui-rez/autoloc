package tn.esprit.autoloc.scheduler;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IContratRepository;
import tn.esprit.autoloc.repository.IReservationRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;
import tn.esprit.autoloc.service.ContratSchedulerService;
import tn.esprit.autoloc.service.VehiculeSchedulerService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SchedulerIntegrationTest {

    private static final LocalDate AUJOURD_HUI = LocalDate.of(2026, 6, 15);
    private static final LocalDate HIER = AUJOURD_HUI.minusDays(1);

    @Autowired
    private ContratSchedulerService contratSchedulerService;

    @Autowired
    private VehiculeSchedulerService vehiculeSchedulerService;

    @Autowired
    private IContratRepository contratRepository;

    @Autowired
    private IReservationRepository reservationRepository;

    @Autowired
    private IVehiculeRepository vehiculeRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void contratsEcheanceJournalisesSansAucuneModification() {
        Contrat debut = creerContrat(AUJOURD_HUI, StatutReservation.CONFIRMEE);
        Contrat fin = creerContrat(AUJOURD_HUI.plusDays(2), StatutReservation.CONFIRMEE);
        creerContrat(AUJOURD_HUI.plusDays(3), StatutReservation.CONFIRMEE);
        creerContrat(HIER, StatutReservation.CONFIRMEE);
        creerContrat(AUJOURD_HUI, StatutReservation.ANNULEE);
        creerContrat(AUJOURD_HUI, StatutReservation.TERMINEE);

        Set<ContratSnapshot> avant = snapshotsContrats();
        List<String> messages = capturerLogs(ContratSchedulerService.class, contratSchedulerService::notifierContratsArrivantAEcheance);
        entityManager.flush();
        entityManager.clear();

        assertThat(snapshotsContrats()).isEqualTo(avant);
        assertThat(messages).anySatisfy(message -> assertThat(message)
                .contains("2 contrat(s) arrivant à échéance entre " + AUJOURD_HUI + " et " + AUJOURD_HUI.plusDays(2)));
        assertThat(messages).anySatisfy(message -> assertThat(message)
                .isEqualTo("Contrat " + debut.getIdContrat() + " : date de fin " + AUJOURD_HUI));
        assertThat(messages).anySatisfy(message -> assertThat(message)
                .isEqualTo("Contrat " + fin.getIdContrat() + " : date de fin " + AUJOURD_HUI.plusDays(2)));
    }

    @Test
    void absenceDeContratsEstJournalisee() {
        List<String> messages = capturerLogs(ContratSchedulerService.class, contratSchedulerService::notifierContratsArrivantAEcheance);

        assertThat(messages).anySatisfy(message -> assertThat(message)
                .isEqualTo("Aucun contrat arrivant à échéance entre " + AUJOURD_HUI + " et " + AUJOURD_HUI.plusDays(2)));
    }

    @Test
    void clotureReelleSurH2RespecteTousLesStatutsEtLesVehicules() {
        Vehicule vLibere = creerVehicule("G-001-AA", StatutVehicule.LOUE);
        Reservation rLibere = creerReservation(vLibere, HIER.minusDays(2), HIER, StatutReservation.CONFIRMEE);

        Vehicule vRetard = creerVehicule("G-002-AA", StatutVehicule.LOUE);
        Reservation rRetard = creerReservation(vRetard, AUJOURD_HUI.minusDays(10), HIER.minusDays(5), StatutReservation.CONFIRMEE);

        Vehicule vAujourdhui = creerVehicule("G-003-AA", StatutVehicule.LOUE);
        Reservation rAujourdhui = creerReservation(vAujourdhui, HIER, AUJOURD_HUI, StatutReservation.CONFIRMEE);

        Vehicule vAnnule = creerVehicule("G-004-AA", StatutVehicule.LOUE);
        Reservation rAnnule = creerReservation(vAnnule, HIER.minusDays(2), HIER, StatutReservation.ANNULEE);

        Vehicule vEnAttente = creerVehicule("G-005-AA", StatutVehicule.LOUE);
        Reservation rEnAttente = creerReservation(vEnAttente, HIER.minusDays(2), HIER, StatutReservation.EN_ATTENTE);

        Vehicule vTermine = creerVehicule("G-006-AA", StatutVehicule.LOUE);
        Reservation rTermine = creerReservation(vTermine, HIER.minusDays(2), HIER, StatutReservation.TERMINEE);

        Vehicule vMaintenance = creerVehicule("G-007-AA", StatutVehicule.MAINTENANCE);
        Reservation rMaintenance = creerReservation(vMaintenance, HIER.minusDays(2), HIER, StatutReservation.CONFIRMEE);

        Vehicule vOccupe = creerVehicule("G-008-AA", StatutVehicule.LOUE);
        Reservation rOccupe = creerReservation(vOccupe, HIER.minusDays(2), HIER, StatutReservation.CONFIRMEE);
        creerReservation(vOccupe, AUJOURD_HUI, AUJOURD_HUI.plusDays(2), StatutReservation.CONFIRMEE);

        Reservation rSansVehicule = creerReservation(null, HIER.minusDays(2), HIER, StatutReservation.CONFIRMEE);

        List<String> messages = capturerLogs(VehiculeSchedulerService.class, vehiculeSchedulerService::cloturerLocationsTerminees);
        entityManager.flush();
        entityManager.clear();

        assertThat(statutReservation(rLibere)).isEqualTo(StatutReservation.TERMINEE);
        assertThat(statutReservation(rRetard)).isEqualTo(StatutReservation.TERMINEE);
        assertThat(statutReservation(rMaintenance)).isEqualTo(StatutReservation.TERMINEE);
        assertThat(statutReservation(rOccupe)).isEqualTo(StatutReservation.TERMINEE);
        assertThat(statutReservation(rSansVehicule)).isEqualTo(StatutReservation.TERMINEE);

        assertThat(statutReservation(rAujourdhui)).isEqualTo(StatutReservation.CONFIRMEE);
        assertThat(statutReservation(rAnnule)).isEqualTo(StatutReservation.ANNULEE);
        assertThat(statutReservation(rEnAttente)).isEqualTo(StatutReservation.EN_ATTENTE);
        assertThat(statutReservation(rTermine)).isEqualTo(StatutReservation.TERMINEE);

        assertThat(statutVehicule(vLibere)).isEqualTo(StatutVehicule.DISPONIBLE);
        assertThat(statutVehicule(vRetard)).isEqualTo(StatutVehicule.DISPONIBLE);
        assertThat(statutVehicule(vAujourdhui)).isEqualTo(StatutVehicule.LOUE);
        assertThat(statutVehicule(vAnnule)).isEqualTo(StatutVehicule.LOUE);
        assertThat(statutVehicule(vEnAttente)).isEqualTo(StatutVehicule.LOUE);
        assertThat(statutVehicule(vTermine)).isEqualTo(StatutVehicule.LOUE);
        assertThat(statutVehicule(vMaintenance)).isEqualTo(StatutVehicule.MAINTENANCE);
        assertThat(statutVehicule(vOccupe)).isEqualTo(StatutVehicule.LOUE);

        assertThat(messages).anySatisfy(message -> assertThat(message)
                .isEqualTo("5 location(s) clôturée(s), 2 véhicule(s) libéré(s) au " + AUJOURD_HUI));
    }

    @Test
    void deuxiemeExecutionReelleNeModifieRien() {
        Vehicule vehicule = creerVehicule("H-001-AA", StatutVehicule.LOUE);
        creerReservation(vehicule, HIER.minusDays(2), HIER, StatutReservation.CONFIRMEE);

        vehiculeSchedulerService.cloturerLocationsTerminees();
        entityManager.flush();
        entityManager.clear();

        Map<Long, StatutReservation> reservationsAvant = statutsReservations();
        Map<Long, StatutVehicule> vehiculesAvant = statutsVehicules();

        assertThatCode(vehiculeSchedulerService::cloturerLocationsTerminees).doesNotThrowAnyException();
        entityManager.flush();
        entityManager.clear();

        assertThat(statutsReservations()).isEqualTo(reservationsAvant);
        assertThat(statutsVehicules()).isEqualTo(vehiculesAvant);
        assertThat(vehiculeRepository.findById(vehicule.getIdVehicule()).orElseThrow().getStatut())
                .isEqualTo(StatutVehicule.DISPONIBLE);
    }

    private Contrat creerContrat(LocalDate dateFin, StatutReservation statut) {
        Reservation reservation = creerReservation(null, dateFin.minusDays(1), dateFin, statut);
        Contrat contrat = new Contrat();
        contrat.setDateSignature(HIER);
        contrat.setMontantTotal(new BigDecimal("10.00"));
        contrat.setValide(true);
        contrat.setReservation(reservation);
        return contratRepository.save(contrat);
    }

    private Vehicule creerVehicule(String immatriculation, StatutVehicule statut) {
        Vehicule vehicule = new Vehicule();
        vehicule.setImmatriculation(immatriculation);
        vehicule.setMarque("Renault");
        vehicule.setModele("Clio");
        vehicule.setCategorie(CategorieVehicule.CITADINE);
        vehicule.setTarifJournalier(BigDecimal.TEN);
        vehicule.setStatut(statut);
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

    private StatutReservation statutReservation(Reservation reservation) {
        return reservationRepository.findById(reservation.getIdReservation()).orElseThrow().getStatut();
    }

    private StatutVehicule statutVehicule(Vehicule vehicule) {
        return vehiculeRepository.findById(vehicule.getIdVehicule()).orElseThrow().getStatut();
    }

    private Set<ContratSnapshot> snapshotsContrats() {
        return contratRepository.findAll().stream()
                .map(contrat -> new ContratSnapshot(
                        contrat.getIdContrat(), contrat.getDateSignature(), contrat.getMontantTotal(),
                        contrat.getValide(), contrat.getReservation().getIdReservation(),
                        contrat.getReservation().getDateDebut(), contrat.getReservation().getDateFin(),
                        contrat.getReservation().getStatut()))
                .collect(Collectors.toSet());
    }

    private Map<Long, StatutReservation> statutsReservations() {
        return reservationRepository.findAll().stream()
                .collect(Collectors.toMap(Reservation::getIdReservation, Reservation::getStatut));
    }

    private Map<Long, StatutVehicule> statutsVehicules() {
        return vehiculeRepository.findAll().stream()
                .collect(Collectors.toMap(Vehicule::getIdVehicule, Vehicule::getStatut));
    }

    private List<String> capturerLogs(Class<?> loggerType, Runnable action) {
        Logger logger = (Logger) LoggerFactory.getLogger(loggerType);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        try {
            action.run();
        } finally {
            logger.detachAppender(appender);
        }
        return appender.list.stream().map(ILoggingEvent::getFormattedMessage).toList();
    }

    private record ContratSnapshot(Long idContrat, LocalDate dateSignature, BigDecimal montantTotal,
                                   Boolean valide, Long idReservation, LocalDate dateDebut,
                                   LocalDate dateFin, StatutReservation statut) {
    }

    @TestConfiguration
    static class FixedClockConfiguration {

        @Bean
        @Primary
        Clock fixedClock() {
            ZoneId zone = ZoneId.of("Africa/Tunis");
            return Clock.fixed(AUJOURD_HUI.atStartOfDay(zone).toInstant(), zone);
        }
    }
}

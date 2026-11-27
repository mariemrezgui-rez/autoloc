package tn.esprit.autoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.StatutReservation;

import java.time.LocalDate;
import java.util.List;

public interface IReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("select r from Reservation r where r.dateDebut = :dateDebut and r.dateFin = :dateFin " +
            "and not exists (select c from Contrat c where c.reservation = r)")
    List<Reservation> findSansContratByDates(@Param("dateDebut") LocalDate dateDebut,
                                             @Param("dateFin") LocalDate dateFin);

    @Query("select r from Reservation r left join fetch r.vehicule " +
            "where r.statut = :statut and r.dateFin < :date")
    List<Reservation> findLocationsTermineesNonCloturees(@Param("statut") StatutReservation statut,
                                                        @Param("date") LocalDate date);

    @Query("select distinct r.vehicule.idVehicule from Reservation r " +
            "where r.vehicule is not null and r.statut = :statut " +
            "and r.dateDebut <= :date and r.dateFin >= :date")
    List<Long> findVehiculeIdsAvecLocationEnCours(@Param("statut") StatutReservation statut,
                                                  @Param("date") LocalDate date);
}

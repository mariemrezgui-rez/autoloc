package tn.esprit.autoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.autoloc.domain.Reservation;

import java.time.LocalDate;
import java.util.List;

public interface IReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("select r from Reservation r where r.dateDebut = :dateDebut and r.dateFin = :dateFin " +
            "and not exists (select c from Contrat c where c.reservation = r)")
    List<Reservation> findSansContratByDates(@Param("dateDebut") LocalDate dateDebut,
                                             @Param("dateFin") LocalDate dateFin);
}

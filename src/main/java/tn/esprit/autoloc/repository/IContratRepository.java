package tn.esprit.autoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.StatutReservation;

import java.time.LocalDate;
import java.util.List;

public interface IContratRepository extends JpaRepository<Contrat, Long> {

    @Query("select c from Contrat c join fetch c.reservation r " +
            "where r.dateFin between :debut and :fin " +
            "and r.statut not in :statutsExclus")
    List<Contrat> findByReservationDateFinBetweenAndReservationStatutNotIn(
            @Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin,
            @Param("statutsExclus") List<StatutReservation> statutsExclus);
}

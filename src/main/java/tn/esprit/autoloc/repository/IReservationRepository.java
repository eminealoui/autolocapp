package tn.esprit.autoloc.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.autoloc.domain.Reservation;

public interface IReservationRepository extends JpaRepository<Reservation, Long> {

    // Atelier 6 : reservations d'une periode donnee qui n'ont pas encore de contrat
    // (affectation d'un contrat a une reservation)
    @Query("select r from Reservation r "
            + "where r.dateDebut = :dateDebut and r.dateFin = :dateFin "
            + "and not exists (select c.idContrat from Contrat c where c.reservation = r) "
            + "order by r.idReservation")
    List<Reservation> findSansContratByPeriode(@Param("dateDebut") LocalDate dateDebut,
                                               @Param("dateFin") LocalDate dateFin);
}

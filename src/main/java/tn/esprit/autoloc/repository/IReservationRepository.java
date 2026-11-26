package tn.esprit.autoloc.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.domain.Vehicule;

public interface IReservationRepository extends JpaRepository<Reservation, Long> {

    // Atelier 6 : reservations d'une periode donnee qui n'ont pas encore de contrat
    // (affectation d'un contrat a une reservation)
    @Query("select r from Reservation r "
            + "where r.dateDebut = :dateDebut and r.dateFin = :dateFin "
            + "and not exists (select c.idContrat from Contrat c where c.reservation = r) "
            + "order by r.idReservation")
    List<Reservation> findSansContratByPeriode(@Param("dateDebut") LocalDate dateDebut,
                                               @Param("dateFin") LocalDate dateFin);

    // Atelier 7 : locations d'un statut donne dont la date de fin est strictement anterieure a une date
    List<Reservation> findByStatutAndDateFinBefore(StatutReservation statut, LocalDate date);

    // Atelier 7 : le vehicule a-t-il une location (statut donne) qui couvre la date indiquee ?
    @Query("select count(r) > 0 from Reservation r "
            + "where r.vehicule = :vehicule and r.statut = :statut "
            + "and r.dateDebut <= :date and r.dateFin >= :date")
    boolean existsLocationEnCours(@Param("vehicule") Vehicule vehicule,
                                  @Param("statut") StatutReservation statut,
                                  @Param("date") LocalDate date);
}

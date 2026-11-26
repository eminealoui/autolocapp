package tn.esprit.autoloc.repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.StatutReservation;

public interface IContratRepository extends JpaRepository<Contrat, Long> {

    // Atelier 7 : contrats dont la reservation se termine entre deux dates (echeance du contrat),
    // hors reservations dont le statut fait partie de "exclus" (ex. ANNULEE, TERMINEE)
    @Query("select c from Contrat c "
            + "join fetch c.reservation r join fetch r.client join fetch r.vehicule "
            + "where r.dateFin between :debut and :fin and r.statut not in (:exclus) "
            + "order by r.dateFin, c.idContrat")
    List<Contrat> findEcheanceEntre(@Param("debut") LocalDate debut,
                                    @Param("fin") LocalDate fin,
                                    @Param("exclus") Collection<StatutReservation> exclus);
}

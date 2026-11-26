package tn.esprit.autoloc.scheduler;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.repository.IContratRepository;

// Tache planifiee : repere les contrats qui arrivent a echeance sous 48 heures.
// L'echeance d'un contrat est la date de fin de sa reservation.
@Slf4j
@Service
@RequiredArgsConstructor
public class ContratSchedulerService {

    private static final ZoneId ZONE = ZoneId.of("Africa/Tunis");

    private final IContratRepository contratRepository;

    // Du lundi au vendredi a 7h00 : secondes minutes heures jour-du-mois mois jour-de-la-semaine
    @Scheduled(cron = "${autoloc.scheduler.contrats.cron:0 0 7 * * MON-FRI}", zone = "Africa/Tunis")
    @Transactional(readOnly = true)
    public void identifierContratsEcheanceSous48h() {
        LocalDate aujourdhui = LocalDate.now(ZONE);
        LocalDate limite = aujourdhui.plusDays(2); // 48 heures

        // les reservations annulees ou deja terminees n'ont plus d'echeance a surveiller
        List<Contrat> contrats = contratRepository.findEcheanceEntre(
                aujourdhui, limite, List.of(StatutReservation.ANNULEE, StatutReservation.TERMINEE));

        if (contrats.isEmpty()) {
            log.info("[Echeances] Aucun contrat n'arrive a echeance d'ici le {}", limite);
            return;
        }
        log.warn("[Echeances] {} contrat(s) arrivent a echeance d'ici le {} :", contrats.size(), limite);
        for (Contrat contrat : contrats) {
            Reservation reservation = contrat.getReservation();
            log.warn("[Echeances] Contrat {} | fin le {} | client {} {} | vehicule {}",
                    contrat.getIdContrat(),
                    reservation.getDateFin(),
                    reservation.getClient().getPrenom(),
                    reservation.getClient().getNom(),
                    reservation.getVehicule().getImmatriculation());
        }
    }
}

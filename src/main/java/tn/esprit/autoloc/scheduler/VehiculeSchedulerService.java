package tn.esprit.autoloc.scheduler;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IReservationRepository;

// Tache planifiee : a la fin d'une location, la reservation passe a TERMINEE
// et le vehicule redevient DISPONIBLE.
@Slf4j
@Service
@RequiredArgsConstructor
public class VehiculeSchedulerService {

    private static final ZoneId ZONE = ZoneId.of("Africa/Tunis");

    private final IReservationRepository reservationRepository;

    // Tous les jours a 1h00 : secondes minutes heures jour-du-mois mois jour-de-la-semaine
    @Scheduled(cron = "${autoloc.scheduler.vehicules.cron:0 0 1 * * *}", zone = "Africa/Tunis")
    @Transactional
    public void cloturerLocationsTerminees() {
        LocalDate aujourdhui = LocalDate.now(ZONE);

        // locations confirmees dont la date de fin est depassee (hier ou avant)
        List<Reservation> terminees = reservationRepository
                .findByStatutAndDateFinBefore(StatutReservation.CONFIRMEE, aujourdhui);

        int vehiculesLiberes = 0;
        for (Reservation reservation : terminees) {
            reservation.setStatut(StatutReservation.TERMINEE);

            Vehicule vehicule = reservation.getVehicule();
            // un vehicule en maintenance le reste ; un vehicule deja reloue aujourd'hui n'est pas libere
            boolean libre = vehicule.getStatut() != StatutVehicule.MAINTENANCE
                    && !reservationRepository.existsLocationEnCours(vehicule, StatutReservation.CONFIRMEE, aujourdhui);
            if (libre && vehicule.getStatut() != StatutVehicule.DISPONIBLE) {
                vehicule.setStatut(StatutVehicule.DISPONIBLE);
                vehiculesLiberes++;
            }
            log.info("[Locations] Reservation {} terminee (fin le {}) | vehicule {} -> {}",
                    reservation.getIdReservation(), reservation.getDateFin(),
                    vehicule.getImmatriculation(), vehicule.getStatut());
        }
        log.info("[Locations] {} location(s) cloturee(s), {} vehicule(s) remis a DISPONIBLE",
                terminees.size(), vehiculesLiberes);
    }
}

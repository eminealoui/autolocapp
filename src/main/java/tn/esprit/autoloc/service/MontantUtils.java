package tn.esprit.autoloc.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// Calcul du montant d'un contrat : nombre de jours de la reservation (minimum 1) x tarif journalier
final class MontantUtils {

    private MontantUtils() {
    }

    static BigDecimal calculer(LocalDate dateDebut, LocalDate dateFin, BigDecimal tarifJournalier) {
        long jours = Math.max(1, ChronoUnit.DAYS.between(dateDebut, dateFin));
        return tarifJournalier.multiply(BigDecimal.valueOf(jours));
    }
}

package tn.esprit.autoloc.demo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.ModePaiement;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.service.IAgenceService;
import tn.esprit.autoloc.service.IClientService;
import tn.esprit.autoloc.service.IContratService;
import tn.esprit.autoloc.service.IPaiementService;
import tn.esprit.autoloc.service.IReservationService;
import tn.esprit.autoloc.service.IVehiculeService;

// Petit programme de test pour verifier le CRUD sur Contrat et Paiement
// et voir la cascade fonctionner (supprimer un contrat supprime ses paiements).
// A desactiver (ou supprimer) une fois les captures faites pour ne pas
// reinserer des donnees de test a chaque demarrage.
@Component
public class CrudDemoRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(CrudDemoRunner.class);
    private static final ZoneId ZONE_TUNIS = ZoneId.of("Africa/Tunis");

    private final IAgenceService agenceService;
    private final IVehiculeService vehiculeService;
    private final IClientService clientService;
    private final IReservationService reservationService;
    private final IContratService contratService;
    private final IPaiementService paiementService;

    public CrudDemoRunner(IAgenceService agenceService, IVehiculeService vehiculeService,
            IClientService clientService, IReservationService reservationService,
            IContratService contratService, IPaiementService paiementService) {
        this.agenceService = agenceService;
        this.vehiculeService = vehiculeService;
        this.clientService = clientService;
        this.reservationService = reservationService;
        this.contratService = contratService;
        this.paiementService = paiementService;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("=== DEBUT TEST CRUD Contrat / Paiement ===");

        // il faut d'abord une agence, un vehicule, un client et une reservation
        // pour pouvoir creer un contrat (voir le diagramme de classes)
        Agence agence = new Agence();
        agence.setNom("AutoLoc Centre");
        agence.setVille("Tunis");
        agence.setAdresse("Avenue Habib Bourguiba");
        agence.setTelephone("71000000");
        agence = agenceService.addAgence(agence);

        Vehicule vehicule = new Vehicule();
        vehicule.setImmatriculation("123TU" + System.currentTimeMillis());
        vehicule.setMarque("Peugeot");
        vehicule.setModele("208");
        vehicule.setCategorie(CategorieVehicule.CITADINE);
        vehicule.setTarifJournalier(new BigDecimal("80.00"));
        vehicule.setStatut(StatutVehicule.DISPONIBLE);
        vehicule.setAgence(agence);
        vehicule = vehiculeService.addVehicule(vehicule);

        Client client = new Client();
        client.setNom("Trabelsi");
        client.setPrenom("Amine");
        client.setEmail("amine" + System.currentTimeMillis() + "@mail.tn");
        client.setTelephone("22000000");
        client.setNumPermis("P" + System.currentTimeMillis());
        client.setDateInscription(LocalDate.now(ZONE_TUNIS));
        client = clientService.addClient(client);

        Reservation reservation = new Reservation();
        reservation.setDateDebut(LocalDate.now(ZONE_TUNIS).plusDays(1));
        reservation.setDateFin(LocalDate.now(ZONE_TUNIS).plusDays(5));
        reservation.setStatut(StatutReservation.CONFIRMEE);
        reservation.setClient(client);
        reservation.setVehicule(vehicule);
        reservation = reservationService.addReservation(reservation);

        // CREATE contrat
        Contrat contrat = new Contrat();
        contrat.setDateSignature(LocalDate.now(ZONE_TUNIS));
        contrat.setMontantTotal(new BigDecimal("320.00"));
        contrat.setValide(true);
        contrat.setReservation(reservation);
        contrat = contratService.addContrat(contrat);
        logger.info("Contrat ajoute, id = {}", contrat.getIdContrat());

        // READ contrat
        Contrat c2 = contratService.retrieveContrat(contrat.getIdContrat());
        logger.info("Contrat lu, montant = {}", c2.getMontantTotal());

        // UPDATE contrat
        c2.setMontantTotal(new BigDecimal("350.00"));
        contratService.updateContrat(c2);
        logger.info("Contrat modifie, nouveau montant = 350.00");

        // CREATE paiements lies au contrat
        Paiement p1 = new Paiement();
        p1.setMontant(new BigDecimal("175.00"));
        p1.setDatePaiement(LocalDate.now(ZONE_TUNIS));
        p1.setModePaiement(ModePaiement.CARTE);
        p1.setContrat(c2);
        p1 = paiementService.addPaiement(p1);
        logger.info("Paiement 1 ajoute, id = {}", p1.getIdPaiement());

        Paiement p2 = new Paiement();
        p2.setMontant(new BigDecimal("175.00"));
        p2.setDatePaiement(LocalDate.now(ZONE_TUNIS).plusDays(4));
        p2.setModePaiement(ModePaiement.ESPECES);
        p2.setContrat(c2);
        p2 = paiementService.addPaiement(p2);
        logger.info("Paiement 2 ajoute, id = {}", p2.getIdPaiement());

        logger.info("Nombre de paiements avant suppression : {}", paiementService.retrieveAllPaiements().size());

        // DELETE du contrat -> doit supprimer les 2 paiements aussi (cascade)
        contratService.removeContrat(c2.getIdContrat());
        logger.info("Contrat supprime");

        logger.info("Nombre de paiements apres suppression : {}", paiementService.retrieveAllPaiements().size());

        logger.info("=== FIN TEST CRUD ===");
    }
}

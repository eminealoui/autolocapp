package tn.esprit.autoloc.service;

import java.util.List;
import tn.esprit.autoloc.domain.Paiement;

// Lecture seule : un paiement se cree, se modifie et se supprime uniquement via son Contrat
// (composition Contrat <> Paiement, cascade = ALL + orphanRemoval). Voir IContratService.
public interface IPaiementService {

    Paiement findById(Long id);

    List<Paiement> findAll();
}

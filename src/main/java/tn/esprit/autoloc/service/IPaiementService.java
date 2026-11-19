package tn.esprit.autoloc.service;

import java.util.List;
import tn.esprit.autoloc.dto.PaiementDTO;

// Lecture seule : un paiement se cree, se modifie et se supprime uniquement via son Contrat
// (composition Contrat <> Paiement, cascade = ALL + orphanRemoval). Voir IContratService.
public interface IPaiementService {

    PaiementDTO findById(Long id);

    List<PaiementDTO> findAll();
}

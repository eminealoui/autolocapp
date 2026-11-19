package tn.esprit.autoloc.service;

import java.time.LocalDate;
import java.util.List;
import tn.esprit.autoloc.dto.ContratDTO;
import tn.esprit.autoloc.dto.ContratRequestDTO;

public interface IContratService {

    ContratDTO create(ContratRequestDTO request);

    ContratDTO findById(Long id);

    List<ContratDTO> findAll();

    ContratDTO update(Long id, ContratRequestDTO request);

    void deleteById(Long id);

    // ---- Atelier 6 : service d'affectation ----

    // Cree un contrat signe a la date donnee et l'affecte a la reservation (sans contrat)
    // dont la periode correspond a dateDebut / dateFin
    ContratDTO affecterContratAReservation(LocalDate dateSignature, LocalDate dateDebut, LocalDate dateFin);
}

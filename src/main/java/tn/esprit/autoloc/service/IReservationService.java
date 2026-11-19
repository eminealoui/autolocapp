package tn.esprit.autoloc.service;

import java.util.List;
import tn.esprit.autoloc.dto.ReservationRequestDTO;
import tn.esprit.autoloc.dto.ReservationResponseDTO;
import tn.esprit.autoloc.dto.ReservationWithContratRequestDTO;

public interface IReservationService {

    ReservationResponseDTO create(ReservationRequestDTO request);

    ReservationResponseDTO findById(Long id);

    List<ReservationResponseDTO> findAll();

    ReservationResponseDTO update(Long id, ReservationRequestDTO request);

    void deleteById(Long id);

    // ---- Atelier 6 : services d'affectation ----

    // Affecte une reservation existante au client identifie par son nom et son prenom
    ReservationResponseDTO affecterReservationAClient(Long idReservation, String nom, String prenom);

    // Cree une reservation et son contrat dans une meme transaction
    ReservationResponseDTO creerReservationEtContratAssocie(ReservationWithContratRequestDTO requestDTO);
}

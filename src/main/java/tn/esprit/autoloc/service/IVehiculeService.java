package tn.esprit.autoloc.service;

import java.util.List;
import tn.esprit.autoloc.dto.VehiculeDTO;
import tn.esprit.autoloc.dto.VehiculeRequestDTO;

public interface IVehiculeService {

    VehiculeDTO create(VehiculeRequestDTO request);

    VehiculeDTO findById(Long id);

    List<VehiculeDTO> findAll();

    VehiculeDTO update(Long id, VehiculeRequestDTO request);

    void deleteById(Long id);

    // ---- Atelier 6 : services d'affectation ----

    // Ajoute un equipement a un vehicule (sans doublon)
    VehiculeDTO affecterVehiculeAEquipement(Long idVehicule, Long idEquipement);

    // Cree une maintenance (debut = aujourd'hui) pour le vehicule identifie par son immatriculation
    // et passe le vehicule au statut MAINTENANCE
    VehiculeDTO affecterMaintenanceAvehicule(String description, String immatriculation);
}

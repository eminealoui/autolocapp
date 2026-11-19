package tn.esprit.autoloc.mapper;

import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.dto.VehiculeDTO;
import tn.esprit.autoloc.dto.VehiculeRequestDTO;

// Conversion Vehicule <-> DTO. L'agence (relation) est affectee par le service.
@Component
@RequiredArgsConstructor
public class VehiculeMapper {

    private final EquipementMapper equipementMapper;

    public VehiculeDTO toDto(Vehicule vehicule) {
        if (vehicule == null) {
            return null;
        }
        return VehiculeDTO.builder()
                .idVehicule(vehicule.getIdVehicule())
                .immatriculation(vehicule.getImmatriculation())
                .marque(vehicule.getMarque())
                .modele(vehicule.getModele())
                .categorie(vehicule.getCategorie())
                .tarifJournalier(vehicule.getTarifJournalier())
                .statut(vehicule.getStatut())
                .idAgence(vehicule.getAgence() != null ? vehicule.getAgence().getIdAgence() : null)
                .nomAgence(vehicule.getAgence() != null ? vehicule.getAgence().getNom() : null)
                .equipements(vehicule.getEquipements().stream()
                        .map(equipementMapper::toDto)
                        .collect(Collectors.toList()))
                .nbMaintenances(vehicule.getMaintenances().size())
                .build();
    }

    public Vehicule toEntity(VehiculeRequestDTO request) {
        Vehicule vehicule = new Vehicule();
        updateEntity(vehicule, request);
        return vehicule;
    }

    public void updateEntity(Vehicule vehicule, VehiculeRequestDTO request) {
        vehicule.setImmatriculation(request.getImmatriculation());
        vehicule.setMarque(request.getMarque());
        vehicule.setModele(request.getModele());
        vehicule.setCategorie(request.getCategorie());
        vehicule.setTarifJournalier(request.getTarifJournalier());
        vehicule.setStatut(request.getStatut());
    }
}

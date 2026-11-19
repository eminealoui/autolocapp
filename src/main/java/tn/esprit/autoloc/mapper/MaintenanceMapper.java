package tn.esprit.autoloc.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.dto.MaintenanceDTO;
import tn.esprit.autoloc.dto.MaintenanceRequestDTO;

// Conversion Maintenance <-> DTO. Le vehicule (relation) est affecte par le service.
@Component
public class MaintenanceMapper {

    public MaintenanceDTO toDto(Maintenance maintenance) {
        if (maintenance == null) {
            return null;
        }
        return MaintenanceDTO.builder()
                .idMaintenance(maintenance.getIdMaintenance())
                .dateDebut(maintenance.getDateDebut())
                .dateFin(maintenance.getDateFin())
                .description(maintenance.getDescription())
                .idVehicule(maintenance.getVehicule() != null ? maintenance.getVehicule().getIdVehicule() : null)
                .immatriculation(maintenance.getVehicule() != null ? maintenance.getVehicule().getImmatriculation() : null)
                .build();
    }

    public Maintenance toEntity(MaintenanceRequestDTO request) {
        Maintenance maintenance = new Maintenance();
        updateEntity(maintenance, request);
        return maintenance;
    }

    public void updateEntity(Maintenance maintenance, MaintenanceRequestDTO request) {
        maintenance.setDateDebut(request.getDateDebut());
        maintenance.setDateFin(request.getDateFin());
        maintenance.setDescription(request.getDescription());
    }
}

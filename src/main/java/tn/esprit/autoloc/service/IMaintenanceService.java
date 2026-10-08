package tn.esprit.autoloc.service;

import java.util.List;
import tn.esprit.autoloc.domain.Maintenance;

public interface IMaintenanceService {
    List<Maintenance> retrieveAllMaintenances();
    Maintenance addMaintenance(Maintenance m);
    Maintenance updateMaintenance(Maintenance m);
    Maintenance retrieveMaintenance(Long idMaintenance);
    void removeMaintenance(Long idMaintenance);
    List<Maintenance> addMaintenances(List<Maintenance> maintenances);
}

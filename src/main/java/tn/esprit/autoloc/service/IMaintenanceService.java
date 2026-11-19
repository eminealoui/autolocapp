package tn.esprit.autoloc.service;

import java.util.List;
import tn.esprit.autoloc.dto.MaintenanceDTO;
import tn.esprit.autoloc.dto.MaintenanceRequestDTO;

public interface IMaintenanceService {

    MaintenanceDTO create(MaintenanceRequestDTO request);

    MaintenanceDTO findById(Long id);

    List<MaintenanceDTO> findAll();

    MaintenanceDTO update(Long id, MaintenanceRequestDTO request);

    void deleteById(Long id);
}

package tn.esprit.autoloc.service;

import java.util.List;
import tn.esprit.autoloc.dto.EquipementDTO;
import tn.esprit.autoloc.dto.EquipementRequestDTO;

public interface IEquipementService {

    EquipementDTO create(EquipementRequestDTO request);

    EquipementDTO findById(Long id);

    List<EquipementDTO> findAll();

    EquipementDTO update(Long id, EquipementRequestDTO request);

    void deleteById(Long id);
}

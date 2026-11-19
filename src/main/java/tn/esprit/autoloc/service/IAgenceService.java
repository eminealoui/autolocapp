package tn.esprit.autoloc.service;

import java.util.List;
import tn.esprit.autoloc.dto.AgenceDTO;
import tn.esprit.autoloc.dto.AgenceRequestDTO;

public interface IAgenceService {

    AgenceDTO create(AgenceRequestDTO request);

    AgenceDTO findById(Long id);

    List<AgenceDTO> findAll();

    AgenceDTO update(Long id, AgenceRequestDTO request);

    void deleteById(Long id);
}

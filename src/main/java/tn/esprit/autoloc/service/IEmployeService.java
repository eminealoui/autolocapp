package tn.esprit.autoloc.service;

import java.util.List;
import tn.esprit.autoloc.dto.EmployeDTO;
import tn.esprit.autoloc.dto.EmployeRequestDTO;

public interface IEmployeService {

    EmployeDTO create(EmployeRequestDTO request);

    EmployeDTO findById(Long id);

    List<EmployeDTO> findAll();

    EmployeDTO update(Long id, EmployeRequestDTO request);

    void deleteById(Long id);
}

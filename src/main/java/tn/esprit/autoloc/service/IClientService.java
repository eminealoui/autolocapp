package tn.esprit.autoloc.service;

import java.util.List;
import tn.esprit.autoloc.dto.ClientDTO;
import tn.esprit.autoloc.dto.ClientRequestDTO;

public interface IClientService {

    ClientDTO create(ClientRequestDTO request);

    ClientDTO findById(Long id);

    List<ClientDTO> findAll();

    ClientDTO update(Long id, ClientRequestDTO request);

    void deleteById(Long id);
}

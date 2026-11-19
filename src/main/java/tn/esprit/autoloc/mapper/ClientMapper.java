package tn.esprit.autoloc.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.dto.ClientDTO;
import tn.esprit.autoloc.dto.ClientRequestDTO;

// Conversion Client <-> DTO
@Component
public class ClientMapper {

    public ClientDTO toDto(Client client) {
        if (client == null) {
            return null;
        }
        return ClientDTO.builder()
                .idClient(client.getIdClient())
                .nom(client.getNom())
                .prenom(client.getPrenom())
                .email(client.getEmail())
                .telephone(client.getTelephone())
                .numPermis(client.getNumPermis())
                .dateInscription(client.getDateInscription())
                .build();
    }

    public Client toEntity(ClientRequestDTO request) {
        Client client = new Client();
        updateEntity(client, request);
        return client;
    }

    public void updateEntity(Client client, ClientRequestDTO request) {
        client.setNom(request.getNom());
        client.setPrenom(request.getPrenom());
        client.setEmail(request.getEmail());
        client.setTelephone(request.getTelephone());
        client.setNumPermis(request.getNumPermis());
        client.setDateInscription(request.getDateInscription());
    }
}

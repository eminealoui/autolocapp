package tn.esprit.autoloc.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.dto.AgenceDTO;
import tn.esprit.autoloc.dto.AgenceRequestDTO;

// Conversion Agence <-> DTO (aucune logique metier ici)
@Component
public class AgenceMapper {

    public AgenceDTO toDto(Agence agence) {
        if (agence == null) {
            return null;
        }
        return AgenceDTO.builder()
                .idAgence(agence.getIdAgence())
                .nom(agence.getNom())
                .ville(agence.getVille())
                .adresse(agence.getAdresse())
                .telephone(agence.getTelephone())
                .build();
    }

    public Agence toEntity(AgenceRequestDTO request) {
        Agence agence = new Agence();
        updateEntity(agence, request);
        return agence;
    }

    public void updateEntity(Agence agence, AgenceRequestDTO request) {
        agence.setNom(request.getNom());
        agence.setVille(request.getVille());
        agence.setAdresse(request.getAdresse());
        agence.setTelephone(request.getTelephone());
    }
}

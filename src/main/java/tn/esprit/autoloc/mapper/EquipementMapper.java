package tn.esprit.autoloc.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.dto.EquipementDTO;
import tn.esprit.autoloc.dto.EquipementRequestDTO;

// Conversion Equipement <-> DTO
@Component
public class EquipementMapper {

    public EquipementDTO toDto(Equipement equipement) {
        if (equipement == null) {
            return null;
        }
        return EquipementDTO.builder()
                .idEquipement(equipement.getIdEquipement())
                .libelle(equipement.getLibelle())
                .build();
    }

    public Equipement toEntity(EquipementRequestDTO request) {
        Equipement equipement = new Equipement();
        updateEntity(equipement, request);
        return equipement;
    }

    public void updateEntity(Equipement equipement, EquipementRequestDTO request) {
        equipement.setLibelle(request.getLibelle());
    }
}

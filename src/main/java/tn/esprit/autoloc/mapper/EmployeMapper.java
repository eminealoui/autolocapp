package tn.esprit.autoloc.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.dto.EmployeDTO;
import tn.esprit.autoloc.dto.EmployeRequestDTO;

// Conversion Employe <-> DTO. L'agence (relation) est affectee par le service.
@Component
public class EmployeMapper {

    public EmployeDTO toDto(Employe employe) {
        if (employe == null) {
            return null;
        }
        return EmployeDTO.builder()
                .idEmploye(employe.getIdEmploye())
                .nom(employe.getNom())
                .prenom(employe.getPrenom())
                .role(employe.getRole())
                .idAgence(employe.getAgence() != null ? employe.getAgence().getIdAgence() : null)
                .nomAgence(employe.getAgence() != null ? employe.getAgence().getNom() : null)
                .build();
    }

    public Employe toEntity(EmployeRequestDTO request) {
        Employe employe = new Employe();
        updateEntity(employe, request);
        return employe;
    }

    public void updateEntity(Employe employe, EmployeRequestDTO request) {
        employe.setNom(request.getNom());
        employe.setPrenom(request.getPrenom());
        employe.setRole(request.getRole());
    }
}

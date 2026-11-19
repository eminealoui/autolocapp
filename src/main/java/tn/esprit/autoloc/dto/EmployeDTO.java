package tn.esprit.autoloc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.autoloc.domain.RoleEmploye;

// Representation d'un employe : l'agence est reduite a son identifiant et son nom.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeDTO {

    private Long idEmploye;

    private String nom;

    private String prenom;

    private RoleEmploye role;

    private Long idAgence;

    private String nomAgence;
}

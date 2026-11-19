package tn.esprit.autoloc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Donnees saisies pour creer ou modifier un equipement.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EquipementRequestDTO {

    @NotBlank(message = "Le libelle est obligatoire")
    @Size(max = 100, message = "Le libelle ne doit pas depasser 100 caracteres")
    private String libelle;
}

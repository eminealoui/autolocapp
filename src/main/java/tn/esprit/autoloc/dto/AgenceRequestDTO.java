package tn.esprit.autoloc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Donnees saisies pour creer ou modifier une agence.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgenceRequestDTO {

    @NotBlank(message = "Le nom de l'agence est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas depasser 100 caracteres")
    private String nom;

    @NotBlank(message = "La ville est obligatoire")
    @Size(max = 50, message = "La ville ne doit pas depasser 50 caracteres")
    private String ville;

    @Size(max = 150, message = "L'adresse ne doit pas depasser 150 caracteres")
    private String adresse;

    @Size(max = 20, message = "Le telephone ne doit pas depasser 20 caracteres")
    @Pattern(regexp = "^$|^[0-9+ ()-]+$", message = "Le telephone ne doit contenir que des chiffres, espaces, +, ( ) ou -")
    private String telephone;
}

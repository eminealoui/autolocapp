package tn.esprit.autoloc.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Donnees saisies pour creer ou modifier un client.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientRequestDTO {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 50, message = "Le nom ne doit pas depasser 50 caracteres")
    private String nom;

    @NotBlank(message = "Le prenom est obligatoire")
    @Size(max = 50, message = "Le prenom ne doit pas depasser 50 caracteres")
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email n'est pas valide")
    @Size(max = 100, message = "L'email ne doit pas depasser 100 caracteres")
    private String email;

    @Size(max = 20, message = "Le telephone ne doit pas depasser 20 caracteres")
    @Pattern(regexp = "^$|^[0-9+ ()-]+$", message = "Le telephone ne doit contenir que des chiffres, espaces, +, ( ) ou -")
    private String telephone;

    @NotBlank(message = "Le numero de permis est obligatoire")
    @Size(max = 30, message = "Le numero de permis ne doit pas depasser 30 caracteres")
    private String numPermis;

    private LocalDate dateInscription;
}

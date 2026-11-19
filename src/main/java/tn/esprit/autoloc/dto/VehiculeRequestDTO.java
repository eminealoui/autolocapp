package tn.esprit.autoloc.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;

// Donnees saisies pour creer ou modifier un vehicule (l'agence est designee par son identifiant, facultatif).
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehiculeRequestDTO {

    @NotBlank(message = "L'immatriculation est obligatoire")
    @Size(max = 20, message = "L'immatriculation ne doit pas depasser 20 caracteres")
    private String immatriculation;

    @NotBlank(message = "La marque est obligatoire")
    @Size(max = 50, message = "La marque ne doit pas depasser 50 caracteres")
    private String marque;

    @NotBlank(message = "Le modele est obligatoire")
    @Size(max = 50, message = "Le modele ne doit pas depasser 50 caracteres")
    private String modele;

    @NotNull(message = "La categorie est obligatoire (BERLINE, CITADINE, SUV ou UTILITAIRE)")
    private CategorieVehicule categorie;

    @NotNull(message = "Le tarif journalier est obligatoire")
    @PositiveOrZero(message = "Le tarif journalier ne peut pas etre negatif")
    @Digits(integer = 8, fraction = 2, message = "Le tarif journalier doit avoir au plus 8 chiffres et 2 decimales")
    private BigDecimal tarifJournalier;

    @NotNull(message = "Le statut est obligatoire (DISPONIBLE, LOUE ou MAINTENANCE)")
    private StatutVehicule statut;

    private Long idAgence;
}

package tn.esprit.autoloc.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Donnees saisies pour creer ou modifier une maintenance (vehicule designe par son identifiant).
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceRequestDTO {

    @NotNull(message = "La date de debut est obligatoire")
    private LocalDate dateDebut;

    private LocalDate dateFin;

    @Size(max = 255, message = "La description ne doit pas depasser 255 caracteres")
    private String description;

    @NotNull(message = "L'identifiant du vehicule est obligatoire")
    private Long idVehicule;

    // Controle croise : la date de fin (facultative) ne peut pas preceder la date de debut
    @JsonIgnore
    @AssertTrue(message = "La date de fin ne peut pas etre anterieure a la date de debut")
    public boolean isPeriodeValide() {
        return dateDebut == null || dateFin == null || !dateFin.isBefore(dateDebut);
    }
}

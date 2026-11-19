package tn.esprit.autoloc.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.autoloc.domain.StatutReservation;

// Donnees saisies pour creer une reservation et son contrat en une seule operation. Le statut est facultatif (EN_ATTENTE par defaut).
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationWithContratRequestDTO {

    @NotNull(message = "La date de debut est obligatoire")
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    private LocalDate dateFin;

    private StatutReservation statut;

    @NotNull(message = "L'identifiant du client est obligatoire")
    private Long idClient;

    @NotNull(message = "L'identifiant du vehicule est obligatoire")
    private Long idVehicule;

    @NotNull(message = "Les informations du contrat sont obligatoires")
    @Valid
    private ContratInfoRequestDTO contrat;

    // Controle croise : la date de fin ne peut pas preceder la date de debut
    @JsonIgnore
    @AssertTrue(message = "La date de fin ne peut pas etre anterieure a la date de debut")
    public boolean isPeriodeValide() {
        return dateDebut == null || dateFin == null || !dateFin.isBefore(dateDebut);
    }
}

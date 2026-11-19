package tn.esprit.autoloc.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.autoloc.domain.StatutReservation;

// Donnees saisies pour creer ou modifier une reservation (client et vehicule designes par leur identifiant).
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequestDTO {

    @NotNull(message = "La date de debut est obligatoire")
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    private LocalDate dateFin;

    @NotNull(message = "Le statut est obligatoire (ANNULEE, CONFIRMEE, EN_ATTENTE ou TERMINEE)")
    private StatutReservation statut;

    @NotNull(message = "L'identifiant du client est obligatoire")
    private Long idClient;

    @NotNull(message = "L'identifiant du vehicule est obligatoire")
    private Long idVehicule;

    // Controle croise : la date de fin ne peut pas preceder la date de debut
    @JsonIgnore
    @AssertTrue(message = "La date de fin ne peut pas etre anterieure a la date de debut")
    public boolean isPeriodeValide() {
        return dateDebut == null || dateFin == null || !dateFin.isBefore(dateDebut);
    }
}

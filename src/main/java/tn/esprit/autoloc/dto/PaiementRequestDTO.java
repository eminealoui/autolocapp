package tn.esprit.autoloc.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.autoloc.domain.ModePaiement;

// Donnees d'un paiement saisi a la creation d'un contrat.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaiementRequestDTO {

    @NotNull(message = "Le montant du paiement est obligatoire")
    @Positive(message = "Le montant du paiement doit etre strictement positif")
    @Digits(integer = 8, fraction = 2, message = "Le montant doit avoir au plus 8 chiffres et 2 decimales")
    private BigDecimal montant;

    @NotNull(message = "La date de paiement est obligatoire")
    private LocalDate datePaiement;

    @NotNull(message = "Le mode de paiement est obligatoire (CARTE, ESPECES ou VIREMENT)")
    private ModePaiement modePaiement;
}

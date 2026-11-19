package tn.esprit.autoloc.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Contrat saisi avec une reservation. Si montantTotal est omis, il est calcule : nombre de jours x tarif journalier du vehicule.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContratInfoRequestDTO {

    @NotNull(message = "La date de signature du contrat est obligatoire")
    private LocalDate dateSignature;

    @PositiveOrZero(message = "Le montant total ne peut pas etre negatif")
    @Digits(integer = 8, fraction = 2, message = "Le montant total doit avoir au plus 8 chiffres et 2 decimales")
    private BigDecimal montantTotal;

    private boolean valide;
}

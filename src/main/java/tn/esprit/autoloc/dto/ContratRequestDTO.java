package tn.esprit.autoloc.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Donnees saisies pour creer ou modifier un contrat. A la modification, seuls dateSignature, montantTotal et valide sont pris en compte (la reservation et les paiements ne changent pas).
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContratRequestDTO {

    @NotNull(message = "La date de signature est obligatoire")
    private LocalDate dateSignature;

    @NotNull(message = "Le montant total est obligatoire")
    @PositiveOrZero(message = "Le montant total ne peut pas etre negatif")
    @Digits(integer = 8, fraction = 2, message = "Le montant total doit avoir au plus 8 chiffres et 2 decimales")
    private BigDecimal montantTotal;

    private boolean valide;

    @NotNull(message = "L'identifiant de la reservation est obligatoire")
    private Long idReservation;

    @Valid
    private List<PaiementRequestDTO> paiements = new ArrayList<>();
}

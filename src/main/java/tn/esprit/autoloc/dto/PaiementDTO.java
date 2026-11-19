package tn.esprit.autoloc.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.autoloc.domain.ModePaiement;

// Representation d'un paiement.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaiementDTO {

    private Long idPaiement;

    private BigDecimal montant;

    private LocalDate datePaiement;

    private ModePaiement modePaiement;

    private Long idContrat;
}

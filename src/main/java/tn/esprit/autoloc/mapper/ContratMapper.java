package tn.esprit.autoloc.mapper;

import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.dto.ContratDTO;
import tn.esprit.autoloc.dto.ContratRequestDTO;

// Conversion Contrat <-> DTO. La reservation (relation) est affectee par le service.
@Component
@RequiredArgsConstructor
public class ContratMapper {

    private final PaiementMapper paiementMapper;

    public ContratDTO toDto(Contrat contrat) {
        if (contrat == null) {
            return null;
        }
        return ContratDTO.builder()
                .idContrat(contrat.getIdContrat())
                .dateSignature(contrat.getDateSignature())
                .montantTotal(contrat.getMontantTotal())
                .valide(contrat.isValide())
                .idReservation(contrat.getReservation() != null ? contrat.getReservation().getIdReservation() : null)
                .paiements(contrat.getPaiements().stream()
                        .map(paiementMapper::toDto)
                        .collect(Collectors.toList()))
                .build();
    }

    public Contrat toEntity(ContratRequestDTO request) {
        Contrat contrat = new Contrat();
        updateEntity(contrat, request);
        return contrat;
    }

    // Seuls les champs propres au contrat sont recopies (ni la reservation, ni les paiements)
    public void updateEntity(Contrat contrat, ContratRequestDTO request) {
        contrat.setDateSignature(request.getDateSignature());
        contrat.setMontantTotal(request.getMontantTotal());
        contrat.setValide(request.isValide());
    }
}

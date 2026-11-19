package tn.esprit.autoloc.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.autoloc.domain.StatutReservation;

// Representation d'une reservation : client et vehicule reduits a l'essentiel, identifiant du contrat associe (null s'il n'y en a pas).
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponseDTO {

    private Long idReservation;

    private LocalDate dateDebut;

    private LocalDate dateFin;

    private StatutReservation statut;

    private Long idClient;

    private String nomClient;

    private Long idVehicule;

    private String immatriculation;

    private Long idContrat;
}

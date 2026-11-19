package tn.esprit.autoloc.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.dto.ReservationRequestDTO;
import tn.esprit.autoloc.dto.ReservationResponseDTO;

// Conversion Reservation <-> DTO. Client et vehicule (relations) sont affectes par le service.
@Component
public class ReservationMapper {

    public ReservationResponseDTO toDto(Reservation reservation) {
        if (reservation == null) {
            return null;
        }
        return ReservationResponseDTO.builder()
                .idReservation(reservation.getIdReservation())
                .dateDebut(reservation.getDateDebut())
                .dateFin(reservation.getDateFin())
                .statut(reservation.getStatut())
                .idClient(reservation.getClient() != null ? reservation.getClient().getIdClient() : null)
                .nomClient(reservation.getClient() != null
                        ? reservation.getClient().getPrenom() + " " + reservation.getClient().getNom()
                        : null)
                .idVehicule(reservation.getVehicule() != null ? reservation.getVehicule().getIdVehicule() : null)
                .immatriculation(reservation.getVehicule() != null ? reservation.getVehicule().getImmatriculation() : null)
                .idContrat(reservation.getContrat() != null ? reservation.getContrat().getIdContrat() : null)
                .build();
    }

    public Reservation toEntity(ReservationRequestDTO request) {
        Reservation reservation = new Reservation();
        updateEntity(reservation, request);
        return reservation;
    }

    public void updateEntity(Reservation reservation, ReservationRequestDTO request) {
        reservation.setDateDebut(request.getDateDebut());
        reservation.setDateFin(request.getDateFin());
        reservation.setStatut(request.getStatut());
    }
}

package tn.esprit.autoloc.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.dto.ContratInfoRequestDTO;
import tn.esprit.autoloc.dto.ReservationRequestDTO;
import tn.esprit.autoloc.dto.ReservationResponseDTO;
import tn.esprit.autoloc.dto.ReservationWithContratRequestDTO;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.mapper.ReservationMapper;
import tn.esprit.autoloc.repository.IClientRepository;
import tn.esprit.autoloc.repository.IContratRepository;
import tn.esprit.autoloc.repository.IReservationRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationServiceImpl implements IReservationService {

    private final IReservationRepository reservationRepository;
    private final IClientRepository clientRepository;
    private final IVehiculeRepository vehiculeRepository;
    private final IContratRepository contratRepository;
    private final ReservationMapper reservationMapper;

    @Override
    public ReservationResponseDTO create(ReservationRequestDTO request) {
        checkPeriode(request.getDateDebut(), request.getDateFin());
        Reservation reservation = reservationMapper.toEntity(request);
        reservation.setClient(getClient(request.getIdClient()));
        reservation.setVehicule(getVehicule(request.getIdVehicule()));
        return reservationMapper.toDto(reservationRepository.save(reservation));
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationResponseDTO findById(Long id) {
        return reservationMapper.toDto(getReservation(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponseDTO> findAll() {
        return reservationRepository.findAll().stream().map(reservationMapper::toDto).toList();
    }

    @Override
    public ReservationResponseDTO update(Long id, ReservationRequestDTO request) {
        Reservation existante = getReservation(id);
        checkPeriode(request.getDateDebut(), request.getDateFin());
        reservationMapper.updateEntity(existante, request);
        existante.setClient(getClient(request.getIdClient()));
        existante.setVehicule(getVehicule(request.getIdVehicule()));
        return reservationMapper.toDto(reservationRepository.save(existante));
    }

    @Override
    public void deleteById(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reservation", id);
        }
        reservationRepository.deleteById(id);
    }

    // ---------------- Atelier 6 : services d'affectation ----------------

    @Override
    public ReservationResponseDTO affecterReservationAClient(Long idReservation, String nom, String prenom) {
        if (!StringUtils.hasText(nom) || !StringUtils.hasText(prenom)) {
            throw new IllegalArgumentException("Le nom et le prenom du client sont obligatoires");
        }
        Reservation reservation = getReservation(idReservation);
        Client client = clientRepository
                .findFirstByNomIgnoreCaseAndPrenomIgnoreCase(nom.trim(), prenom.trim())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Client introuvable avec le nom " + nom.trim() + " et le prenom " + prenom.trim()));
        reservation.setClient(client);
        return reservationMapper.toDto(reservationRepository.save(reservation));
    }

    // Une seule transaction : si la creation du contrat echoue, la reservation est annulee (rollback)
    @Override
    public ReservationResponseDTO creerReservationEtContratAssocie(ReservationWithContratRequestDTO requestDTO) {
        checkPeriode(requestDTO.getDateDebut(), requestDTO.getDateFin());
        Client client = getClient(requestDTO.getIdClient());
        Vehicule vehicule = getVehicule(requestDTO.getIdVehicule());

        Reservation reservation = new Reservation();
        reservation.setDateDebut(requestDTO.getDateDebut());
        reservation.setDateFin(requestDTO.getDateFin());
        reservation.setStatut(requestDTO.getStatut() != null ? requestDTO.getStatut() : StatutReservation.EN_ATTENTE);
        reservation.setClient(client);
        reservation.setVehicule(vehicule);
        Reservation reservationSauvee = reservationRepository.save(reservation);

        ContratInfoRequestDTO infos = requestDTO.getContrat();
        Contrat contrat = new Contrat();
        contrat.setDateSignature(infos.getDateSignature());
        contrat.setValide(infos.isValide());
        contrat.setMontantTotal(infos.getMontantTotal() != null
                ? infos.getMontantTotal()
                : MontantUtils.calculer(reservation.getDateDebut(), reservation.getDateFin(), vehicule.getTarifJournalier()));
        contrat.setReservation(reservationSauvee);
        contratRepository.save(contrat);

        reservationSauvee.setContrat(contrat);
        return reservationMapper.toDto(reservationSauvee);
    }

    private Reservation getReservation(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", id));
    }

    private Client getClient(Long idClient) {
        return clientRepository.findById(idClient)
                .orElseThrow(() -> new ResourceNotFoundException("Client", idClient));
    }

    private Vehicule getVehicule(Long idVehicule) {
        return vehiculeRepository.findById(idVehicule)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", idVehicule));
    }

    // Regle de gestion : la date de fin ne peut pas preceder la date de debut
    // (egalement controlee sur le DTO ; garde-fou cote service)
    private void checkPeriode(LocalDate dateDebut, LocalDate dateFin) {
        if (dateDebut != null && dateFin != null && dateFin.isBefore(dateDebut)) {
            throw new IllegalArgumentException("La date de fin ne peut pas etre anterieure a la date de debut");
        }
    }
}

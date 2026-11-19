package tn.esprit.autoloc.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.dto.ContratDTO;
import tn.esprit.autoloc.dto.ContratRequestDTO;
import tn.esprit.autoloc.dto.PaiementRequestDTO;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.mapper.ContratMapper;
import tn.esprit.autoloc.mapper.PaiementMapper;
import tn.esprit.autoloc.repository.IContratRepository;
import tn.esprit.autoloc.repository.IReservationRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class ContratServiceImpl implements IContratService {

    private final IContratRepository contratRepository;
    private final IReservationRepository reservationRepository;
    private final ContratMapper contratMapper;
    private final PaiementMapper paiementMapper;

    @Override
    public ContratDTO create(ContratRequestDTO request) {
        Contrat contrat = contratMapper.toEntity(request);
        contrat.setReservation(getReservation(request.getIdReservation()));
        // composition : les paiements saisis sont rattaches au contrat et enregistres en cascade
        if (request.getPaiements() != null) {
            for (PaiementRequestDTO paiementRequest : request.getPaiements()) {
                Paiement paiement = paiementMapper.toEntity(paiementRequest);
                paiement.setContrat(contrat);
                contrat.getPaiements().add(paiement);
            }
        }
        return contratMapper.toDto(contratRepository.save(contrat));
    }

    @Override
    @Transactional(readOnly = true)
    public ContratDTO findById(Long id) {
        return contratMapper.toDto(getContrat(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContratDTO> findAll() {
        return contratRepository.findAll().stream().map(contratMapper::toDto).toList();
    }

    @Override
    public ContratDTO update(Long id, ContratRequestDTO request) {
        Contrat existant = getContrat(id);
        // on ne touche ni a la reservation, ni a la collection de paiements
        contratMapper.updateEntity(existant, request);
        return contratMapper.toDto(contratRepository.save(existant));
    }

    @Override
    public void deleteById(Long id) {
        if (!contratRepository.existsById(id)) {
            throw new ResourceNotFoundException("Contrat", id);
        }
        // deleteById charge l'entite puis la supprime : la cascade (CascadeType.ALL +
        // orphanRemoval sur Contrat.paiements) s'applique donc bien aux paiements lies.
        contratRepository.deleteById(id);
    }

    // ---------------- Atelier 6 : service d'affectation ----------------

    @Override
    public ContratDTO affecterContratAReservation(LocalDate dateSignature, LocalDate dateDebut, LocalDate dateFin) {
        if (dateSignature == null || dateDebut == null || dateFin == null) {
            throw new IllegalArgumentException("La date de signature, la date de debut et la date de fin sont obligatoires");
        }
        if (dateFin.isBefore(dateDebut)) {
            throw new IllegalArgumentException("La date de fin ne peut pas etre anterieure a la date de debut");
        }
        // reservations de cette periode qui n'ont pas encore de contrat (la plus ancienne en premier)
        List<Reservation> candidates = reservationRepository.findSansContratByPeriode(dateDebut, dateFin);
        if (candidates.isEmpty()) {
            throw new ResourceNotFoundException("Aucune reservation sans contrat trouvee pour la periode du "
                    + dateDebut + " au " + dateFin);
        }
        Reservation reservation = candidates.get(0);

        Contrat contrat = new Contrat();
        contrat.setDateSignature(dateSignature);
        contrat.setMontantTotal(MontantUtils.calculer(
                reservation.getDateDebut(), reservation.getDateFin(), reservation.getVehicule().getTarifJournalier()));
        contrat.setValide(true);
        contrat.setReservation(reservation);
        return contratMapper.toDto(contratRepository.save(contrat));
    }

    private Contrat getContrat(Long id) {
        return contratRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat", id));
    }

    private Reservation getReservation(Long idReservation) {
        return reservationRepository.findById(idReservation)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", idReservation));
    }
}

package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.autoloc.dto.ReservationRequestDTO;
import tn.esprit.autoloc.dto.ReservationResponseDTO;
import tn.esprit.autoloc.dto.ReservationWithContratRequestDTO;
import tn.esprit.autoloc.service.IReservationService;

// Role du controleur : exposer le service (pas de logique metier). Il recoit et renvoie des DTO.
@Tag(name = "Reservations", description = "Gestion des reservations de vehicules")
@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
@Validated
public class ReservationController {

    private final IReservationService reservationService;

    @Operation(summary = "Lister les reservations")
    @ApiResponse(responseCode = "200", description = "Liste des reservations (eventuellement vide)")
    @GetMapping
    public List<ReservationResponseDTO> findAll() {
        return reservationService.findAll();
    }

    @Operation(summary = "Consulter une reservation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consultation reussie"),
            @ApiResponse(responseCode = "404", description = "Reservation introuvable")
    })
    @GetMapping("/{id}")
    public ReservationResponseDTO findById(@Parameter(description = "Identifiant de la reservation") @PathVariable Long id) {
        return reservationService.findById(id);
    }

    @Operation(summary = "Creer une reservation", description = "Les donnees saisies sont controlees (Bean Validation) ; l'identifiant est genere.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Creation reussie"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides (dates, statut, client ou vehicule manquants, date de fin anterieure a la date de debut)"),
            @ApiResponse(responseCode = "409", description = "Valeur deja utilisee (contrainte d'unicite)")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponseDTO create(@Valid @RequestBody ReservationRequestDTO request) {
        return reservationService.create(request);
    }

    @Operation(summary = "Modifier une reservation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Modification reussie"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides (dates, statut, client ou vehicule manquants, date de fin anterieure a la date de debut)"),
            @ApiResponse(responseCode = "404", description = "Reservation introuvable"),
            @ApiResponse(responseCode = "409", description = "Valeur deja utilisee (contrainte d'unicite)")
    })
    @PutMapping("/{id}")
    public ReservationResponseDTO update(@Parameter(description = "Identifiant de la reservation") @PathVariable Long id,
                       @Valid @RequestBody ReservationRequestDTO request) {
        return reservationService.update(id, request);
    }

    @Operation(summary = "Supprimer une reservation")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Suppression reussie"),
            @ApiResponse(responseCode = "404", description = "Reservation introuvable")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = "Identifiant de la reservation") @PathVariable Long id) {
        reservationService.deleteById(id);
    }

    // ---------------- Atelier 6 : services d'affectation ----------------

    @Operation(summary = "Affecter une reservation a un client",
            description = "Le client est recherche par son nom et son prenom.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reservation affectee au client"),
            @ApiResponse(responseCode = "400", description = "Nom ou prenom manquant"),
            @ApiResponse(responseCode = "404", description = "Reservation ou client introuvable")
    })
    @PutMapping("/{idReservation}/affecter-client")
    public ReservationResponseDTO affecterReservationAClient(
            @Parameter(description = "Identifiant de la reservation") @PathVariable Long idReservation,
            @Parameter(description = "Nom du client") @RequestParam
            @NotBlank(message = "Le nom du client est obligatoire") String nom,
            @Parameter(description = "Prenom du client") @RequestParam
            @NotBlank(message = "Le prenom du client est obligatoire") String prenom) {
        return reservationService.affecterReservationAClient(idReservation, nom, prenom);
    }

    @Operation(summary = "Creer une reservation et son contrat",
            description = "Cree la reservation et le contrat associe dans une seule transaction. "
                    + "Si montantTotal est omis, il est calcule (jours x tarif journalier du vehicule).")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Reservation et contrat crees"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides"),
            @ApiResponse(responseCode = "404", description = "Client ou vehicule introuvable")
    })
    @PostMapping("/avec-contrat")
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponseDTO creerReservationEtContratAssocie(
            @Valid @RequestBody ReservationWithContratRequestDTO requestDTO) {
        return reservationService.creerReservationEtContratAssocie(requestDTO);
    }
}

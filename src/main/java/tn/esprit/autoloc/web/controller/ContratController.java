package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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
import tn.esprit.autoloc.dto.ContratDTO;
import tn.esprit.autoloc.dto.ContratRequestDTO;
import tn.esprit.autoloc.service.IContratService;

// Role du controleur : exposer le service (pas de logique metier). Il recoit et renvoie des DTO.
@Tag(name = "Contrats", description = "Gestion des contrats de location")
@RestController
@RequestMapping("/contrats")
@RequiredArgsConstructor
@Validated
public class ContratController {

    private final IContratService contratService;

    @Operation(summary = "Lister les contrats")
    @ApiResponse(responseCode = "200", description = "Liste des contrats (eventuellement vide)")
    @GetMapping
    public List<ContratDTO> findAll() {
        return contratService.findAll();
    }

    @Operation(summary = "Consulter un contrat")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consultation reussie"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable")
    })
    @GetMapping("/{id}")
    public ContratDTO findById(@Parameter(description = "Identifiant du contrat") @PathVariable Long id) {
        return contratService.findById(id);
    }

    @Operation(summary = "Creer un contrat", description = "Les donnees saisies sont controlees (Bean Validation) ; l'identifiant est genere.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Creation reussie"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides (date, montant negatif, reservation ou paiements incorrects)"),
            @ApiResponse(responseCode = "409", description = "Valeur deja utilisee (contrainte d'unicite)")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContratDTO create(@Valid @RequestBody ContratRequestDTO request) {
        return contratService.create(request);
    }

    @Operation(summary = "Modifier un contrat")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Modification reussie"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides (date, montant negatif, reservation ou paiements incorrects)"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable"),
            @ApiResponse(responseCode = "409", description = "Valeur deja utilisee (contrainte d'unicite)")
    })
    @PutMapping("/{id}")
    public ContratDTO update(@Parameter(description = "Identifiant du contrat") @PathVariable Long id,
                       @Valid @RequestBody ContratRequestDTO request) {
        return contratService.update(id, request);
    }

    @Operation(summary = "Supprimer un contrat")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Suppression reussie"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = "Identifiant du contrat") @PathVariable Long id) {
        contratService.deleteById(id);
    }

    // ---------------- Atelier 6 : service d'affectation ----------------

    @Operation(summary = "Affecter un contrat a une reservation",
            description = "Cree un contrat signe a la date donnee pour la reservation (sans contrat) dont la periode "
                    + "est dateDebut -> dateFin. Montant = jours x tarif journalier du vehicule.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Contrat cree et affecte a la reservation"),
            @ApiResponse(responseCode = "400", description = "Parametre manquant, mal forme ou dates incoherentes"),
            @ApiResponse(responseCode = "404", description = "Aucune reservation sans contrat pour cette periode")
    })
    @PostMapping("/affecter-reservation")
    @ResponseStatus(HttpStatus.CREATED)
    public ContratDTO affecterContratAReservation(
            @Parameter(description = "Date de signature (AAAA-MM-JJ)", example = "2026-11-02")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateSignature,
            @Parameter(description = "Date de debut de la reservation (AAAA-MM-JJ)", example = "2026-11-02")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @Parameter(description = "Date de fin de la reservation (AAAA-MM-JJ)", example = "2026-11-06")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        return contratService.affecterContratAReservation(dateSignature, dateDebut, dateFin);
    }
}

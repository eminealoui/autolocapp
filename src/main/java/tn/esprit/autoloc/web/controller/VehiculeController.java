package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
import tn.esprit.autoloc.dto.VehiculeDTO;
import tn.esprit.autoloc.dto.VehiculeRequestDTO;
import tn.esprit.autoloc.service.IVehiculeService;

// Role du controleur : exposer le service (pas de logique metier). Il recoit et renvoie des DTO.
@Tag(name = "Vehicules", description = "Gestion du parc de vehicules")
@RestController
@RequestMapping("/vehicules")
@RequiredArgsConstructor
@Validated
public class VehiculeController {

    private final IVehiculeService vehiculeService;

    @Operation(summary = "Lister les vehicules")
    @ApiResponse(responseCode = "200", description = "Liste des vehicules (eventuellement vide)")
    @GetMapping
    public List<VehiculeDTO> findAll() {
        return vehiculeService.findAll();
    }

    @Operation(summary = "Consulter un vehicule")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consultation reussie"),
            @ApiResponse(responseCode = "404", description = "Vehicule introuvable")
    })
    @GetMapping("/{id}")
    public VehiculeDTO findById(@Parameter(description = "Identifiant du vehicule") @PathVariable Long id) {
        return vehiculeService.findById(id);
    }

    @Operation(summary = "Creer un vehicule", description = "Les donnees saisies sont controlees (Bean Validation) ; l'identifiant est genere.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Creation reussie"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides (immatriculation vide, tarif negatif, categorie ou statut manquant)"),
            @ApiResponse(responseCode = "409", description = "Valeur deja utilisee (contrainte d'unicite)")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehiculeDTO create(@Valid @RequestBody VehiculeRequestDTO request) {
        return vehiculeService.create(request);
    }

    @Operation(summary = "Modifier un vehicule")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Modification reussie"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides (immatriculation vide, tarif negatif, categorie ou statut manquant)"),
            @ApiResponse(responseCode = "404", description = "Vehicule introuvable"),
            @ApiResponse(responseCode = "409", description = "Valeur deja utilisee (contrainte d'unicite)")
    })
    @PutMapping("/{id}")
    public VehiculeDTO update(@Parameter(description = "Identifiant du vehicule") @PathVariable Long id,
                       @Valid @RequestBody VehiculeRequestDTO request) {
        return vehiculeService.update(id, request);
    }

    @Operation(summary = "Supprimer un vehicule")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Suppression reussie"),
            @ApiResponse(responseCode = "404", description = "Vehicule introuvable")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = "Identifiant du vehicule") @PathVariable Long id) {
        vehiculeService.deleteById(id);
    }

    // ---------------- Atelier 6 : services d'affectation ----------------

    @Operation(summary = "Affecter un equipement a un vehicule",
            description = "Ajoute l'equipement a la liste des equipements du vehicule (sans doublon).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Equipement affecte, vehicule renvoye"),
            @ApiResponse(responseCode = "404", description = "Vehicule ou equipement introuvable")
    })
    @PutMapping("/{idVehicule}/equipements/{idEquipement}")
    public VehiculeDTO affecterVehiculeAEquipement(
            @Parameter(description = "Identifiant du vehicule") @PathVariable Long idVehicule,
            @Parameter(description = "Identifiant de l'equipement") @PathVariable Long idEquipement) {
        return vehiculeService.affecterVehiculeAEquipement(idVehicule, idEquipement);
    }

    @Operation(summary = "Affecter une maintenance a un vehicule",
            description = "Cree une maintenance (debut = aujourd'hui) pour le vehicule identifie par son immatriculation "
                    + "et passe le vehicule au statut MAINTENANCE.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Maintenance creee, vehicule renvoye"),
            @ApiResponse(responseCode = "400", description = "Parametre manquant ou invalide"),
            @ApiResponse(responseCode = "404", description = "Aucun vehicule avec cette immatriculation")
    })
    @PostMapping("/affecter-maintenance")
    public VehiculeDTO affecterMaintenanceAvehicule(
            @Parameter(description = "Description de la maintenance") @RequestParam
            @Size(max = 255, message = "La description ne doit pas depasser 255 caracteres") String description,
            @Parameter(description = "Immatriculation du vehicule") @RequestParam
            @NotBlank(message = "L'immatriculation est obligatoire") String immatriculation) {
        return vehiculeService.affecterMaintenanceAvehicule(description, immatriculation);
    }
}

package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.service.IEquipementService;

@Tag(name = "Equipements", description = "Gestion des equipements des vehicules")
@RestController
@RequestMapping("/equipements")
@RequiredArgsConstructor
public class EquipementController {

    private final IEquipementService equipementService;

    @Operation(summary = "Lister les equipements")
    @ApiResponse(responseCode = "200", description = "Liste des equipements (eventuellement vide)")
    @GetMapping
    public List<Equipement> findAll() {
        return equipementService.findAll();
    }

    @Operation(summary = "Consulter un equipement")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Equipement trouve"),
            @ApiResponse(responseCode = "404", description = "Equipement introuvable")
    })
    @GetMapping("/{id}")
    public Equipement findById(@Parameter(description = "Identifiant de l'equipement") @PathVariable Long id) {
        return equipementService.findById(id);
    }

    @Operation(summary = "Creer un equipement", description = "L'identifiant ne doit pas etre fourni.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Equipement cree"),
            @ApiResponse(responseCode = "400", description = "Identifiant fourni ou regle de gestion violee (libelle vide)")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Equipement create(@RequestBody Equipement equipement) {
        return equipementService.create(equipement);
    }

    @Operation(summary = "Modifier un equipement")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Equipement modifie"),
            @ApiResponse(responseCode = "400", description = "Regle de gestion violee (libelle vide)"),
            @ApiResponse(responseCode = "404", description = "Equipement introuvable")
    })
    @PutMapping("/{id}")
    public Equipement update(@Parameter(description = "Identifiant de l'equipement") @PathVariable Long id,
                       @RequestBody Equipement equipement) {
        return equipementService.update(id, equipement);
    }

    @Operation(summary = "Supprimer un equipement")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Equipement supprime"),
            @ApiResponse(responseCode = "404", description = "Equipement introuvable")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = "Identifiant de l'equipement") @PathVariable Long id) {
        equipementService.deleteById(id);
    }
}

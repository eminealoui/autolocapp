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
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.service.IEmployeService;

@Tag(name = "Employes", description = "Gestion des employes des agences")
@RestController
@RequestMapping("/employes")
@RequiredArgsConstructor
public class EmployeController {

    private final IEmployeService employeService;

    @Operation(summary = "Lister les employes")
    @ApiResponse(responseCode = "200", description = "Liste des employes (eventuellement vide)")
    @GetMapping
    public List<Employe> findAll() {
        return employeService.findAll();
    }

    @Operation(summary = "Consulter un employe")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employe trouve"),
            @ApiResponse(responseCode = "404", description = "Employe introuvable")
    })
    @GetMapping("/{id}")
    public Employe findById(@Parameter(description = "Identifiant de l'employe") @PathVariable Long id) {
        return employeService.findById(id);
    }

    @Operation(summary = "Creer un employe", description = "L'identifiant ne doit pas etre fourni.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Employe cree"),
            @ApiResponse(responseCode = "400", description = "Identifiant fourni ou regle de gestion violee (nom, prenom ou role vide)")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Employe create(@RequestBody Employe employe) {
        return employeService.create(employe);
    }

    @Operation(summary = "Modifier un employe")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employe modifie"),
            @ApiResponse(responseCode = "400", description = "Regle de gestion violee (nom, prenom ou role vide)"),
            @ApiResponse(responseCode = "404", description = "Employe introuvable")
    })
    @PutMapping("/{id}")
    public Employe update(@Parameter(description = "Identifiant de l'employe") @PathVariable Long id,
                       @RequestBody Employe employe) {
        return employeService.update(id, employe);
    }

    @Operation(summary = "Supprimer un employe")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Employe supprime"),
            @ApiResponse(responseCode = "404", description = "Employe introuvable")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = "Identifiant de l'employe") @PathVariable Long id) {
        employeService.deleteById(id);
    }
}

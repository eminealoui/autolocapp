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
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.service.IReservationService;

@Tag(name = "Reservations", description = "Gestion des reservations de vehicules")
@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final IReservationService reservationService;

    @Operation(summary = "Lister les reservations")
    @ApiResponse(responseCode = "200", description = "Liste des reservations (eventuellement vide)")
    @GetMapping
    public List<Reservation> findAll() {
        return reservationService.findAll();
    }

    @Operation(summary = "Consulter une reservation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reservation trouvee"),
            @ApiResponse(responseCode = "404", description = "Reservation introuvable")
    })
    @GetMapping("/{id}")
    public Reservation findById(@Parameter(description = "Identifiant de la reservation") @PathVariable Long id) {
        return reservationService.findById(id);
    }

    @Operation(summary = "Creer une reservation", description = "L'identifiant ne doit pas etre fourni.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Reservation creee"),
            @ApiResponse(responseCode = "400", description = "Identifiant fourni ou regle de gestion violee (date de fin anterieure a la date de debut)")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Reservation create(@RequestBody Reservation reservation) {
        return reservationService.create(reservation);
    }

    @Operation(summary = "Modifier une reservation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reservation modifiee"),
            @ApiResponse(responseCode = "400", description = "Regle de gestion violee (date de fin anterieure a la date de debut)"),
            @ApiResponse(responseCode = "404", description = "Reservation introuvable")
    })
    @PutMapping("/{id}")
    public Reservation update(@Parameter(description = "Identifiant de la reservation") @PathVariable Long id,
                       @RequestBody Reservation reservation) {
        return reservationService.update(id, reservation);
    }

    @Operation(summary = "Supprimer une reservation")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Reservation supprimee"),
            @ApiResponse(responseCode = "404", description = "Reservation introuvable")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = "Identifiant de la reservation") @PathVariable Long id) {
        reservationService.deleteById(id);
    }
}

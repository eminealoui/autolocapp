package tn.esprit.autoloc.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;

// Representation d'un vehicule : agence reduite a (id, nom), equipements affectes et nombre de maintenances.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehiculeDTO {

    private Long idVehicule;

    private String immatriculation;

    private String marque;

    private String modele;

    private CategorieVehicule categorie;

    private BigDecimal tarifJournalier;

    private StatutVehicule statut;

    private Long idAgence;

    private String nomAgence;

    @Builder.Default
    private List<EquipementDTO> equipements = new ArrayList<>();

    private int nbMaintenances;
}

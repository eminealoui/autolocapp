package tn.esprit.autoloc.service;

import java.util.List;
import tn.esprit.autoloc.domain.Equipement;

public interface IEquipementService {
    List<Equipement> retrieveAllEquipements();
    Equipement addEquipement(Equipement e);
    Equipement updateEquipement(Equipement e);
    Equipement retrieveEquipement(Long idEquipement);
    void removeEquipement(Long idEquipement);
    List<Equipement> addEquipements(List<Equipement> equipements);
}

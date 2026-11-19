package tn.esprit.autoloc.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.autoloc.domain.Vehicule;

public interface IVehiculeRepository extends JpaRepository<Vehicule, Long> {

    // Atelier 6 : recherche par immatriculation (affectation d'une maintenance)
    Optional<Vehicule> findByImmatriculation(String immatriculation);
}

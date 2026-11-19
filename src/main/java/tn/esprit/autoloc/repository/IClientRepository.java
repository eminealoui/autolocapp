package tn.esprit.autoloc.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.autoloc.domain.Client;

public interface IClientRepository extends JpaRepository<Client, Long> {

    // Atelier 6 : recherche par nom et prenom (affectation d'une reservation a un client)
    Optional<Client> findFirstByNomIgnoreCaseAndPrenomIgnoreCase(String nom, String prenom);
}

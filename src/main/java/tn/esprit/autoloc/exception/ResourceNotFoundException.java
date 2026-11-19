package tn.esprit.autoloc.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " introuvable avec l'identifiant " + id);
    }

    // Ressource recherchee selon un autre critere (nom, immatriculation, periode...)
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

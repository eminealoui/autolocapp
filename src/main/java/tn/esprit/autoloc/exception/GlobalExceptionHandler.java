package tn.esprit.autoloc.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

// Gestion centralisee des erreurs de TOUS les controleurs (Atelier 6).
// Les controleurs et les services n'ont pas de try/catch : ils levent une exception,
// et cette classe la traduit en reponse HTTP avec un corps JSON uniforme (ErrorResponse).
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 : ressource metier introuvable (id inconnu, client/vehicule introuvable...)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
    }

    // 400 : echec de la validation du corps de la requete (@Valid sur un DTO)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> fieldErrors.merge(error.getField(), String.valueOf(error.getDefaultMessage()),
                        (ancien, nouveau) -> ancien + " ; " + nouveau));
        ex.getBindingResult().getGlobalErrors()
                .forEach(error -> fieldErrors.merge(error.getObjectName(), String.valueOf(error.getDefaultMessage()),
                        (ancien, nouveau) -> ancien + " ; " + nouveau));
        return build(HttpStatus.BAD_REQUEST, "Les donnees saisies sont invalides", request, fieldErrors);
    }

    // 400 : echec de la validation des parametres (@RequestParam / @PathVariable avec contraintes)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String chemin = violation.getPropertyPath().toString();
            String champ = chemin.substring(chemin.lastIndexOf('.') + 1);
            fieldErrors.merge(champ, violation.getMessage(), (ancien, nouveau) -> ancien + " ; " + nouveau);
        });
        return build(HttpStatus.BAD_REQUEST, "Les parametres de la requete sont invalides", request, fieldErrors);
    }

    // 400 : JSON mal forme, date illisible, valeur d'enumeration inconnue...
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST,
                "Corps de la requete illisible : JSON mal forme, date au format AAAA-MM-JJ attendu ou valeur d'enumeration inconnue",
                request, null);
    }

    // 400 : parametre d'URL du mauvais type (ex. /vehicules/abc, date mal formee)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String message = "Le parametre '" + ex.getName() + "' a une valeur invalide : " + ex.getValue();
        return build(HttpStatus.BAD_REQUEST, message, request, null);
    }

    // 400 : parametre obligatoire absent
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "Le parametre obligatoire '" + ex.getParameterName() + "' est manquant", request, null);
    }

    // 400 : regle de gestion violee dans un service
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request, null);
    }

    // 404 : URL inconnue
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResource(NoResourceFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "Aucune ressource pour l'URL demandee", request, null);
    }

    // 405 : methode HTTP non autorisee (ex. POST /paiements)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, "Methode HTTP non autorisee : " + ex.getMethod(), request, null);
    }

    // 415 : Content-Type non supporte (JSON attendu)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaType(HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Type de contenu non supporte : application/json attendu", request, null);
    }

    // 409 : contrainte de la base violee (immatriculation / email / permis deja utilises, suppression d'une ligne referencee...)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Violation de contrainte d'integrite : {}", ex.getMostSpecificCause().getMessage());
        return build(HttpStatus.CONFLICT,
                "Operation impossible : une valeur unique est deja utilisee ou l'element est reference par d'autres donnees",
                request, null);
    }

    // 500 : toute autre erreur imprevue (le detail technique reste dans les logs, pas dans la reponse)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Erreur inattendue sur {}", request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur interne du serveur", request, null);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, HttpServletRequest request,
                                                Map<String, String> fieldErrors) {
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .fieldErrors(fieldErrors)
                .build();
        return ResponseEntity.status(status).body(body);
    }
}

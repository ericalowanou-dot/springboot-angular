package com.ucao.dgi.l3.exception;

import jakarta.servlet.ServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/** Transforme les exceptions en réponses JSON homogènes : { status, message, erreurs, horodatage }. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RessourceIntrouvableException.class)
    public ResponseEntity<Map<String, Object>> introuvable(RessourceIntrouvableException e) {
        return reponse(HttpStatus.NOT_FOUND, e.getMessage(), null);
    }

    @ExceptionHandler(RegleMetierException.class)
    public ResponseEntity<Map<String, Object>> regleMetier(RegleMetierException e) {
        return reponse(HttpStatus.CONFLICT, e.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException e) {
        Map<String, String> erreurs = new LinkedHashMap<>();
        for (FieldError fe : e.getBindingResult().getFieldErrors()) {
            erreurs.putIfAbsent(fe.getField(), fe.getDefaultMessage());
        }
        String message = erreurs.isEmpty() ? "Données invalides" : erreurs.values().iterator().next();
        return reponse(HttpStatus.BAD_REQUEST, message, erreurs);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            IllegalArgumentException.class})
    public ResponseEntity<Map<String, Object>> requeteInvalide(Exception e) {
        return reponse(HttpStatus.BAD_REQUEST, "Requête invalide", null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> integrite(DataIntegrityViolationException e) {
        log.warn("Violation d'intégrité : {}", e.getMostSpecificCause().getMessage());
        return reponse(HttpStatus.CONFLICT,
                "Opération impossible : cet élément existe déjà ou est utilisé ailleurs", null);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> fichierTropGros(MaxUploadSizeExceededException e) {
        return reponse(HttpStatus.PAYLOAD_TOO_LARGE, "Fichier trop volumineux (5 Mo maximum)", null);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> identifiants(BadCredentialsException e) {
        return reponse(HttpStatus.UNAUTHORIZED, e.getMessage(), null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> accesRefuse(AccessDeniedException e) {
        return reponse(HttpStatus.FORBIDDEN, "Accès refusé", null);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> routeInconnue(NoResourceFoundException e) {
        return reponse(HttpStatus.NOT_FOUND, "Ressource introuvable", null);
    }

    /** Erreurs MVC standard (méthode non supportée, paramètre manquant...) : on garde leur statut HTTP. */
    @ExceptionHandler(ServletException.class)
    public ResponseEntity<Map<String, Object>> erreurMvc(ServletException e) {
        HttpStatus status = e instanceof ErrorResponse er
                ? HttpStatus.valueOf(er.getStatusCode().value())
                : HttpStatus.BAD_REQUEST;
        return reponse(status, "Requête invalide", null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> inattendue(Exception e) {
        log.error("Erreur inattendue", e);
        return reponse(HttpStatus.INTERNAL_SERVER_ERROR, "Une erreur interne est survenue", null);
    }

    private ResponseEntity<Map<String, Object>> reponse(HttpStatus status, String message, Map<String, String> erreurs) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status.value());
        body.put("message", message);
        if (erreurs != null) {
            body.put("erreurs", erreurs);
        }
        body.put("horodatage", LocalDateTime.now().toString());
        return ResponseEntity.status(status).body(body);
    }
}

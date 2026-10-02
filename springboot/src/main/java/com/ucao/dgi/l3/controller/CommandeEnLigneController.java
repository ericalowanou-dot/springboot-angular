package com.ucao.dgi.l3.controller;

import com.ucao.dgi.l3.dto.EnLigneDtos.CommandeEnLigneRequest;
import com.ucao.dgi.l3.dto.EnLigneDtos.CommandeEnLigneResponse;
import com.ucao.dgi.l3.dto.EnLigneDtos.SuiviCommande;
import com.ucao.dgi.l3.security.LimiteurRequetes;
import com.ucao.dgi.l3.service.CommandeEnLigneService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/** Routes publiques (sans compte) : commander depuis la carte et suivre sa commande. */
@RestController
@RequestMapping("/api/public/commandes")
@RequiredArgsConstructor
public class CommandeEnLigneController {

    private final CommandeEnLigneService commandeEnLigneService;
    private final LimiteurRequetes limiteur;

    @PostMapping
    public ResponseEntity<?> commander(@Valid @RequestBody CommandeEnLigneRequest requete, HttpServletRequest http) {
        if (!limiteur.autoriser(http)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of(
                    "status", 429,
                    "message", "Trop de commandes envoyées. Réessayez dans quelques minutes ou appelez le restaurant.",
                    "horodatage", LocalDateTime.now().toString()));
        }
        CommandeEnLigneResponse reponse = commandeEnLigneService.commander(requete);
        return ResponseEntity.status(HttpStatus.CREATED).body(reponse);
    }

    @GetMapping("/{code}")
    public SuiviCommande suivre(@PathVariable String code) {
        return commandeEnLigneService.suivre(code);
    }
}

package com.ucao.dgi.l3.controller;

import com.ucao.dgi.l3.dto.CommandeDtos.CommandeRequest;
import com.ucao.dgi.l3.dto.CommandeDtos.PaiementRequest;
import com.ucao.dgi.l3.dto.CommandeDtos.StatutRequest;
import com.ucao.dgi.l3.entity.Commande;
import com.ucao.dgi.l3.entity.StatutCommande;
import com.ucao.dgi.l3.service.CommandeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commandes")
@RequiredArgsConstructor
public class CommandeController {

    private final CommandeService commandeService;

    @GetMapping
    public List<Commande> findAll(@RequestParam(required = false) StatutCommande statut) {
        return commandeService.findAll(statut);
    }

    @GetMapping("/{id}")
    public Commande findById(@PathVariable Integer id) {
        return commandeService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Commande creer(@Valid @RequestBody CommandeRequest requete) {
        return commandeService.creer(requete);
    }

    @PutMapping("/{id}")
    public Commande modifier(@PathVariable Integer id, @Valid @RequestBody CommandeRequest requete) {
        return commandeService.modifier(id, requete);
    }

    @PatchMapping("/{id}/statut")
    public Commande changerStatut(@PathVariable Integer id, @Valid @RequestBody StatutRequest requete) {
        return commandeService.changerStatut(id, requete.statut());
    }

    @PostMapping("/{id}/paiement")
    public Commande payer(@PathVariable Integer id, @Valid @RequestBody PaiementRequest requete) {
        return commandeService.payer(id, requete.methode());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        commandeService.delete(id);
    }
}

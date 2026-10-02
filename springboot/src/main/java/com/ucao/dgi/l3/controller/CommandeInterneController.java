package com.ucao.dgi.l3.controller;

import com.ucao.dgi.l3.dto.CommandeDtos.ApprovisionnementRequest;
import com.ucao.dgi.l3.entity.CommandeInterne;
import com.ucao.dgi.l3.service.CommandeInterneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Approvisionnements auprès des fournisseurs (commandes internes). */
@RestController
@RequestMapping("/api/approvisionnements")
@RequiredArgsConstructor
public class CommandeInterneController {

    private final CommandeInterneService commandeInterneService;

    @GetMapping
    public List<CommandeInterne> findAll() {
        return commandeInterneService.findAll();
    }

    @GetMapping("/{id}")
    public CommandeInterne findById(@PathVariable Integer id) {
        return commandeInterneService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommandeInterne creer(@Valid @RequestBody ApprovisionnementRequest requete) {
        return commandeInterneService.creer(requete);
    }

    /** Marque la commande comme reçue et ajoute les quantités au stock. */
    @PostMapping("/{id}/reception")
    public CommandeInterne recevoir(@PathVariable Integer id) {
        return commandeInterneService.recevoir(id);
    }

    @PostMapping("/{id}/annulation")
    public CommandeInterne annuler(@PathVariable Integer id) {
        return commandeInterneService.annuler(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        commandeInterneService.delete(id);
    }
}

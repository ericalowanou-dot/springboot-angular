package com.ucao.dgi.l3.controller;

import com.ucao.dgi.l3.dto.CommandeDtos.EchecRequest;
import com.ucao.dgi.l3.entity.Livraison;
import com.ucao.dgi.l3.entity.User;
import com.ucao.dgi.l3.service.LivraisonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Espace du livreur connecté (rôle LIVREUR) : uniquement ses propres livraisons. */
@RestController
@RequestMapping("/api/livreur/livraisons")
@RequiredArgsConstructor
public class LivreurController {

    private final LivraisonService livraisonService;

    @GetMapping
    public List<Livraison> mesLivraisons(@AuthenticationPrincipal User livreur) {
        return livraisonService.mesLivraisons(livreur);
    }

    @GetMapping("/disponibles")
    public List<Livraison> disponibles() {
        return livraisonService.disponibles();
    }

    @PostMapping("/{id}/prendre")
    public Livraison prendre(@AuthenticationPrincipal User livreur, @PathVariable Integer id) {
        return livraisonService.prendre(livreur, id);
    }

    @PostMapping("/{id}/liberer")
    public Livraison liberer(@AuthenticationPrincipal User livreur, @PathVariable Integer id) {
        return livraisonService.liberer(livreur, id);
    }

    @PostMapping("/{id}/depart")
    public Livraison depart(@AuthenticationPrincipal User livreur, @PathVariable Integer id) {
        return livraisonService.depart(livreur, id);
    }

    @PostMapping("/{id}/livree")
    public Livraison livree(@AuthenticationPrincipal User livreur, @PathVariable Integer id) {
        return livraisonService.livree(livreur, id);
    }

    @PostMapping("/{id}/echec")
    public Livraison echec(@AuthenticationPrincipal User livreur, @PathVariable Integer id,
                           @Valid @RequestBody EchecRequest requete) {
        return livraisonService.echec(livreur, id, requete.motif(), requete.commentaire());
    }
}

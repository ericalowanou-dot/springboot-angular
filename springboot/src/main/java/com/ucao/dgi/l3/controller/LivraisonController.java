package com.ucao.dgi.l3.controller;

import com.ucao.dgi.l3.dto.CommandeDtos.LivraisonUpdateRequest;
import com.ucao.dgi.l3.entity.Livraison;
import com.ucao.dgi.l3.service.LivraisonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Les livraisons sont créées automatiquement avec les commandes de type LIVRAISON. */
@RestController
@RequestMapping("/api/livraisons")
@RequiredArgsConstructor
public class LivraisonController {

    private final LivraisonService livraisonService;

    @GetMapping
    public List<Livraison> findAll() {
        return livraisonService.findAll();
    }

    @GetMapping("/{id}")
    public Livraison findById(@PathVariable Integer id) {
        return livraisonService.findById(id);
    }

    @PatchMapping("/{id}")
    public Livraison mettreAJour(@PathVariable Integer id, @RequestBody LivraisonUpdateRequest requete) {
        return livraisonService.mettreAJour(id, requete);
    }
}

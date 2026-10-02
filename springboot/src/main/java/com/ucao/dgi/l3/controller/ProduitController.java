package com.ucao.dgi.l3.controller;

import com.ucao.dgi.l3.dto.CommandeDtos.AjustementStockRequest;
import com.ucao.dgi.l3.dto.CommandeDtos.ProduitRequest;
import com.ucao.dgi.l3.entity.Produit;
import com.ucao.dgi.l3.service.ProduitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Produits (matières premières) et leur stock. */
@RestController
@RequestMapping("/api/produits")
@RequiredArgsConstructor
public class ProduitController {

    private final ProduitService produitService;

    @GetMapping
    public List<Produit> findAll() {
        return produitService.findAll();
    }

    @GetMapping("/alertes")
    public List<Produit> alertes() {
        return produitService.findEnAlerte();
    }

    @GetMapping("/{id}")
    public Produit findById(@PathVariable Integer id) {
        return produitService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Produit save(@Valid @RequestBody ProduitRequest requete) {
        return produitService.save(requete);
    }

    @PutMapping("/{id}")
    public Produit update(@PathVariable Integer id, @Valid @RequestBody ProduitRequest requete) {
        return produitService.update(id, requete);
    }

    @PatchMapping("/{id}/stock")
    public Produit ajusterStock(@PathVariable Integer id, @Valid @RequestBody AjustementStockRequest requete) {
        return produitService.ajusterStock(id, requete.quantite());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        produitService.delete(id);
    }
}

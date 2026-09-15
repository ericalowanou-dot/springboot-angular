package com.ucao.dgi.l3.controller;

import com.ucao.dgi.l3.entity.Produit;
import com.ucao.dgi.l3.service.impl.ProduitServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/produits")
@CrossOrigin("*")
@RestController
public class ProduitController {

    @Autowired
    private ProduitServiceImpl produitServiceImpl;

    @GetMapping("/find_all")
    public List<Produit> findAll() {
        return produitServiceImpl.findAll();
    }

    @GetMapping("/find_by_id/{id}")
    public Produit findById(@PathVariable Integer id) {
        return produitServiceImpl.findById(id);
    }

    @PostMapping("/save")
    public Produit save(@RequestBody Produit produit) {
        return produitServiceImpl.save(produit);
    }

    @PutMapping("/update/{id}")
    public Produit update(@PathVariable Integer id, @RequestBody Produit produit) {
        produit.setIdProduit(id);
        return produitServiceImpl.update(produit);
    }

    @DeleteMapping("/delete/{id}")
    public Produit delete(@PathVariable Integer id) {
        Produit produit = new Produit();
        produit.setIdProduit(id);
        return produitServiceImpl.delete(produit);
    }

    @GetMapping("/find_by_fournisseur/{idFournisseur}")
    public List<Produit> findByFournisseurId(@PathVariable Integer idFournisseur) {
        return produitServiceImpl.findByFournisseurId(idFournisseur);
    }

    @GetMapping("/find_by_nom/{nomProduit}")
    public List<Produit> findByProduitName(@PathVariable String nomProduit) {
        return produitServiceImpl.findByProduitName(nomProduit);
    }

    @GetMapping("/find_produits_sous_seuil")
    public List<Produit> findProduitsSousSeuil() {
        return produitServiceImpl.findProduitsSousSeuil();
    }
}


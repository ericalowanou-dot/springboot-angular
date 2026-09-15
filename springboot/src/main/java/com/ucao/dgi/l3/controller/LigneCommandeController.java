package com.ucao.dgi.l3.controller;

import com.ucao.dgi.l3.entity.LigneCommande;
import com.ucao.dgi.l3.service.impl.LigneCommandeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/ligne_commandes")
@CrossOrigin("*")
@RestController
public class LigneCommandeController {

    @Autowired
    private LigneCommandeServiceImpl ligneCommandeServiceImpl;

    @GetMapping("/find_all_by_commande/{idCommande}")
    public List<LigneCommande> findAllByCommande(@PathVariable Integer idCommande) {
        return ligneCommandeServiceImpl.findAllByCommande(idCommande);
    }

    @GetMapping("/find_by_id/{id}")
    public LigneCommande findById(@PathVariable Integer id) {
        return ligneCommandeServiceImpl.findById(id);
    }

    @PostMapping("/save")
    public LigneCommande save(@RequestBody LigneCommande ligneCommande) {
        System.out.println("=== LigneCommandeController.save appelé ===");
        System.out.println("LigneCommande reçue: " + ligneCommande);
        LigneCommande saved = ligneCommandeServiceImpl.save(ligneCommande);
        System.out.println("LigneCommande sauvegardée: " + saved);
        return saved;
    }

    @PutMapping("/update/{id}")
    public LigneCommande update(@PathVariable Integer id, @RequestBody LigneCommande ligneCommande) {
        ligneCommande.setIdLigne(id);
        return ligneCommandeServiceImpl.update(ligneCommande);
    }

    @DeleteMapping("/delete/{id}")
    public LigneCommande delete(@PathVariable Integer id) {
        LigneCommande ligneCommande = ligneCommandeServiceImpl.findById(id);
        if (ligneCommande == null) {
            return null;
        }
        return ligneCommandeServiceImpl.delete(ligneCommande);
    }

    @PostMapping("/calculer_sous_total/{idLigne}")
    public LigneCommande calculerSousTotal(@PathVariable Integer idLigne) {
        return ligneCommandeServiceImpl.calculerSousTotal(idLigne);
    }
}

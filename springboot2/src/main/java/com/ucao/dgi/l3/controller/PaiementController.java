package com.ucao.dgi.l3.controller;

import com.ucao.dgi.l3.entity.Paiement;
import com.ucao.dgi.l3.service.impl.PaiementServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequestMapping("/paiements")
@CrossOrigin("*")
@RestController
public class PaiementController {

    @Autowired
    private PaiementServiceImpl paiementServiceImpl;

    @GetMapping("/find_all")
    public List<Paiement> findAll() {
        return paiementServiceImpl.findAllPaiements();
    }

    @GetMapping("/find_by_id/{idPaiement}")
    public Paiement findById(@PathVariable Integer idPaiement) {
        return paiementServiceImpl.findPaiementById(idPaiement);
    }

    @PostMapping("/save")
    public Paiement save(@RequestBody Paiement paiement) {
        System.out.println("=== PaiementController.save appelé ===");
        System.out.println("Paiement reçu: " + paiement);
        Paiement saved = paiementServiceImpl.savePaiement(paiement);
        System.out.println("Paiement sauvegardé: " + saved);
        return saved;
    }

    @PutMapping("/update/{idPaiement}")
    public Paiement update(@PathVariable Integer idPaiement, @RequestBody Paiement paiement) {
        paiement.setIdPaiement(idPaiement);
        return paiementServiceImpl.updatePaiement(paiement);
    }

    @DeleteMapping("/delete/{idPaiement}")
    public Paiement delete(@PathVariable Integer idPaiement) {
        Paiement paiement = new Paiement();
        paiement.setIdPaiement(idPaiement);
        return paiementServiceImpl.deletePaiement(paiement);
    }

    @GetMapping("/find_by_commande/{idCommande}")
    public Paiement findByCommandeId(@PathVariable Integer idCommande) {
        return paiementServiceImpl.findByCommandeId(idCommande);
    }

    @GetMapping("/find_by_date/{datePaiement}")
    public Paiement findByDatePaiement(@PathVariable String datePaiement) {
        return paiementServiceImpl.findByDatePaiement(LocalDate.parse(datePaiement));
    }

    @GetMapping("/find_by_methode/{methode}")
    public Paiement findByMethode(@PathVariable String methode) {
        return paiementServiceImpl.findByMethode(methode);
    }
}


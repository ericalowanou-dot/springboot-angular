package com.ucao.dgi.l3.controller;

import com.ucao.dgi.l3.entity.Commande;
import com.ucao.dgi.l3.service.impl.CommandeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/commandes")
@CrossOrigin("*")
@RestController
public class CommandeController {

    @Autowired
    private CommandeServiceImpl commandeServiceImpl;

    @GetMapping("/find_all")
    public List<Commande> findAll() {
        return commandeServiceImpl.findAll();
    }

    @GetMapping("/find_by_id/{id}")
    public Commande findById(@PathVariable Integer id) {
        return commandeServiceImpl.findById(id);
    }

    @PostMapping("/save")
    public Commande save(@RequestBody Commande commande) {
        return commandeServiceImpl.save(commande);
    }

    @PutMapping("/update/{id}")
    public Commande update(@PathVariable Integer id, @RequestBody Commande commande) {
        commande.setIdCommande(id);
        return commandeServiceImpl.update(commande);
    }

    @DeleteMapping("/delete/{id}")
    public Commande delete(@PathVariable Integer id) {
        Commande commande = new Commande();
        commande.setIdCommande(id);
        return commandeServiceImpl.delete(commande);
    }

    @GetMapping("/find_all_by_client/{idClient}")
    public List<Commande> findAllByClient(@PathVariable Integer idClient) {
        return commandeServiceImpl.findAllByClient(idClient);
    }

    @GetMapping("/find_by_etat/{etat}")
    public List<Commande> findByEtat(@PathVariable String etat) {
        return commandeServiceImpl.findByEtat(etat);
    }

    @PostMapping("/calculer_montant_total")
    public Double calculerMontantTotal(@RequestBody Commande commande) {
        return commandeServiceImpl.calculerMontantTotal(commande);
    }

    @PostMapping("/valider")
    public Commande validerCommande(@RequestBody Commande commande) {
        return commandeServiceImpl.validerCommande(commande);
    }
}

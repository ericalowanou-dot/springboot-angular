package com.ucao.dgi.l3.controller;

import com.ucao.dgi.l3.entity.Fournisseur;
import com.ucao.dgi.l3.service.impl.FournisseurServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/fournisseur")
@CrossOrigin("*")
@RestController

public class FournisseurController {
    @Autowired
    private FournisseurServiceImpl fournisseurServiceImpl;

    @GetMapping("/find_by_id/{idFournisseur}")
    public Fournisseur FindByIdFournisseur(@PathVariable Integer idFournisseur) {
        return fournisseurServiceImpl.findFournisseurById(idFournisseur);
    }

    @PostMapping("/save")
    public Fournisseur Save(@RequestBody Fournisseur fournisseur) {
        return fournisseurServiceImpl.save(fournisseur);
    }

    @PutMapping("/update/{idFournisseur}")
    public Fournisseur Update(@PathVariable Integer idFournisseur, @RequestBody Fournisseur fournisseur) {
        fournisseur.setIdFournisseur(idFournisseur);
        return fournisseurServiceImpl.update(fournisseur);
    }

    @DeleteMapping("/delete/{idFournisseur}")
    public Fournisseur Delete(@PathVariable Integer idFournisseur) {
        Fournisseur fournisseur = new Fournisseur();
        fournisseur.setIdFournisseur(idFournisseur);
        return fournisseurServiceImpl.delete(fournisseur);
    }

    @GetMapping("/find_all")
    public List<Fournisseur> findAll() {
        return fournisseurServiceImpl.findAllFournisseur();
    }
}

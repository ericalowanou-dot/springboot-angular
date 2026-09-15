package com.ucao.dgi.l3.controller;

import com.ucao.dgi.l3.entity.LigneCommandeInterne;
import com.ucao.dgi.l3.service.impl.LigneCommandeInterneServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/ligne_commandes_internes")
@CrossOrigin("*")
@RestController
public class LigneCommandeInterneController {

    @Autowired
    private LigneCommandeInterneServiceImpl ligneCommandeInterneServiceImpl;

    @GetMapping("/find_all")
    public List<LigneCommandeInterne> findAll() {
        return ligneCommandeInterneServiceImpl.findAllCommandeInterne();
    }

    @GetMapping("/find_by_id/{idLigneCommandeInterne}")
    public LigneCommandeInterne findById(@PathVariable Integer idLigneCommandeInterne) {
        return ligneCommandeInterneServiceImpl.findLigneCommandeInterneById(idLigneCommandeInterne);
    }

    @PostMapping("/save")
    public LigneCommandeInterne save(@RequestBody LigneCommandeInterne ligneCommandeInterne) {
        return ligneCommandeInterneServiceImpl.save(ligneCommandeInterne);
    }

    @PutMapping("/update/{idLigneCommandeInterne}")
    public LigneCommandeInterne update(@PathVariable Integer idLigneCommandeInterne, @RequestBody LigneCommandeInterne ligneCommandeInterne) {
        ligneCommandeInterne.setIdLigneInt(idLigneCommandeInterne);
        return ligneCommandeInterneServiceImpl.update(ligneCommandeInterne);
    }

    @DeleteMapping("/delete/{idLigneCommandeInterne}")
    public LigneCommandeInterne delete(@PathVariable Integer idLigneCommandeInterne) {
        LigneCommandeInterne ligneCommandeInterne = ligneCommandeInterneServiceImpl.findLigneCommandeInterneById(idLigneCommandeInterne);
        if (ligneCommandeInterne == null) {
            return null;
        }
        return ligneCommandeInterneServiceImpl.delete(ligneCommandeInterne);
    }

    @PostMapping("/calculer_sous_total/{idLigneCommandeInterne}")
    public LigneCommandeInterne calculerSousTotal(@PathVariable Integer idLigneCommandeInterne) {
        return ligneCommandeInterneServiceImpl.calculerSousTotal(idLigneCommandeInterne);
    }
}


package com.ucao.dgi.l3.controller;

import com.ucao.dgi.l3.entity.CommandeInterne;
import com.ucao.dgi.l3.entity.Fournisseur;
import com.ucao.dgi.l3.service.impl.CommandeInterneServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/commandes_internes")
@CrossOrigin("*")
@RestController
public class CommandeInterneController {

    @Autowired
    private CommandeInterneServiceImpl commandeInterneServiceImpl;

    @GetMapping("/find_all")
    public List<CommandeInterne> findAll() {
        return commandeInterneServiceImpl.findAllCommandeInterne();
    }

    @GetMapping("/find_by_id/{id}")
    public CommandeInterne findById(@PathVariable Integer id) {
        return commandeInterneServiceImpl.findCommandeInterneById(id);
    }

    @PostMapping("/save")
    public CommandeInterne save(@RequestBody CommandeInterne commandeInterne) {
        return commandeInterneServiceImpl.save(commandeInterne);
    }

    @PutMapping("/update/{id}")
    public CommandeInterne update(@PathVariable Integer id, @RequestBody CommandeInterne commandeInterne) {
        commandeInterne.setIdCmdInt(id);
        return commandeInterneServiceImpl.update(commandeInterne);
    }

    @DeleteMapping("/delete/{id}")
    public CommandeInterne delete(@PathVariable Integer id) {
        CommandeInterne commandeInterne = new CommandeInterne();
        commandeInterne.setIdCmdInt(id);
        return commandeInterneServiceImpl.delete(commandeInterne);
    }

    @GetMapping("/find_fournisseur/{idFournisseur}")
    public Fournisseur findFournisseur(@PathVariable Integer idFournisseur) {
        return commandeInterneServiceImpl.findFournisseur(idFournisseur);
    }

    @PostMapping("/valider/{idCommandeInterne}")
    public Fournisseur validerCommandeInterne(@PathVariable Integer idCommandeInterne) {
        return commandeInterneServiceImpl.validerCommandeInterne(idCommandeInterne);
    }
}


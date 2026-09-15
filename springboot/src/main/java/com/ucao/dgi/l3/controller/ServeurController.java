package com.ucao.dgi.l3.controller;

import com.ucao.dgi.l3.entity.Serveur;
import com.ucao.dgi.l3.service.impl.ServeurServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/serveurs")
@CrossOrigin("*")
@RestController
public class ServeurController {

    @Autowired
    private ServeurServiceImpl serveurServiceImpl;

    @GetMapping("/find_all")
    public List<Serveur> findAll() {
        return serveurServiceImpl.findAllServeurs();
    }

    @GetMapping("/find_by_id/{id}")
    public Serveur findById(@PathVariable String id) {
        return serveurServiceImpl.findServeurById(id);
    }

    @PostMapping("/save")
    public Serveur save(@RequestBody Serveur serveur) {
        return serveurServiceImpl.saveServeur(serveur);
    }

    @PostMapping("/update")
    public Serveur update(@RequestBody Serveur serveur) {
        return serveurServiceImpl.updateServeur(serveur);
    }

    @PostMapping("/delete/{idServeur}")
    public Serveur delete(@PathVariable String idServeur) {
        return serveurServiceImpl.deleteServeur(idServeur);
    }
}

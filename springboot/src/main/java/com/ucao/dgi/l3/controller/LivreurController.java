package com.ucao.dgi.l3.controller;

import com.ucao.dgi.l3.entity.Livreur;
import com.ucao.dgi.l3.service.impl.LivreurServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/livreurs")
@CrossOrigin("*")
@RestController
public class LivreurController {

    @Autowired
    private LivreurServiceImpl livreurServiceImpl;

    @GetMapping("/find_all")
    public List<Livreur> findAll() {
        return livreurServiceImpl.findAllLivreurs();
    }

    @GetMapping("/find_by_id/{id}")
    public Livreur findById(@PathVariable String id) {
        return livreurServiceImpl.findLivreurById(id);
    }

    @PostMapping("/save")
    public Livreur save(@RequestBody Livreur livreur) {
        return livreurServiceImpl.saveLivreur(livreur);
    }

    @PostMapping("/update")
    public Livreur update(@RequestBody Livreur livreur) {
        return livreurServiceImpl.updateLivreur(livreur);
    }

    @PostMapping("/delete/{idLivreur}")
    public Livreur delete(@PathVariable String idLivreur) {
        return livreurServiceImpl.deleteLivreur(idLivreur);
    }
}


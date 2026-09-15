package com.ucao.dgi.l3.controller;

import com.ucao.dgi.l3.entity.Plat;
import com.ucao.dgi.l3.service.impl.PlatServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/plats")
@CrossOrigin("*")
@RestController
public class PlatController {

    @Autowired
    private PlatServiceImpl platServiceImpl;

    @GetMapping("/find_all")
    public List<Plat> findAll() {
        return platServiceImpl.findAll();
    }

    @GetMapping("/find_by_id/{idPlat}")
    public Plat findById(@PathVariable Integer idPlat) {
        return platServiceImpl.findById(idPlat);
    }

    @PostMapping("/save")
    public Plat save(@RequestBody Plat plat) {
        return platServiceImpl.save(plat);
    }

    @PutMapping("/update/{idPlat}")
    public Plat update(@PathVariable Integer idPlat, @RequestBody Plat plat) {
        plat.setIdPlat(idPlat);
        return platServiceImpl.update(plat);
    }

    @DeleteMapping("/delete/{idPlat}")
    public Plat delete(@PathVariable Integer idPlat) {
        Plat plat = new Plat();
        plat.setIdPlat(idPlat);
        return platServiceImpl.delete(plat);
    }

    @GetMapping("/find_by_categorie/{idCategorie}")
    public Plat findByCategorie(@PathVariable Integer idCategorie) {
        return platServiceImpl.findByCaterory(idCategorie);
    }

    @GetMapping("/find_by_nom/{nomPlat}")
    public Plat findByNomPlat(@PathVariable String nomPlat) {
        return platServiceImpl.findByNomPlat(nomPlat);
    }

    @GetMapping("/find_by_prix_between/{min}/{max}")
    public Plat findPlatBetween(@PathVariable Double min, @PathVariable Double max) {
        return platServiceImpl.findPlatBetween(min, max);
    }
}

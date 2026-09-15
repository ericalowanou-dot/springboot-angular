package com.ucao.dgi.l3.controller;

import com.ucao.dgi.l3.entity.CategoriePlat;
import com.ucao.dgi.l3.entity.Plat;
import com.ucao.dgi.l3.service.impl.CategoriePlatServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/categorie_plats")
@CrossOrigin("*")
@RestController
public class CategoriePlatController {

    @Autowired
    private CategoriePlatServiceImpl categoriePlatServiceImpl;

    @GetMapping("/find_all")
    public List<CategoriePlat> findAll() {
        return categoriePlatServiceImpl.findAll();
    }

    @GetMapping("/find_by_id/{id}")
    public CategoriePlat findById(@PathVariable Integer id) {
        return categoriePlatServiceImpl.findById(id);
    }

    @PostMapping("/save")
    public CategoriePlat save(@RequestBody CategoriePlat categoriePlat) {
        return categoriePlatServiceImpl.save(categoriePlat);
    }

    @PutMapping("/update/{id}")
    public CategoriePlat update(@PathVariable Integer id, @RequestBody CategoriePlat categoriePlat) {
        categoriePlat.setIdCategorie(id);
        return categoriePlatServiceImpl.update(categoriePlat);
    }

    @DeleteMapping("/delete/{id}")
    public CategoriePlat delete(@PathVariable Integer id) {
        CategoriePlat categoriePlat = new CategoriePlat();
        categoriePlat.setIdCategorie(id);
        return categoriePlatServiceImpl.delete(categoriePlat);
    }

    @GetMapping("/find_by_nom/{nomCategorie}")
    public CategoriePlat findByNomCategorie(@PathVariable String nomCategorie) {
        return categoriePlatServiceImpl.findByNomCategorie(nomCategorie);
    }

    @GetMapping("/find_all_plats/{idCategorie}")
    public List<Plat> findAllPlatsInCategorie(@PathVariable Integer idCategorie) {
        return categoriePlatServiceImpl.findAllPlatsInCategorie(idCategorie);
    }
}


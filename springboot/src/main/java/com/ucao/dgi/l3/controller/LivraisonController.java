package com.ucao.dgi.l3.controller;

import com.ucao.dgi.l3.entity.Livraison;
import com.ucao.dgi.l3.service.impl.LivraisonServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequestMapping("/livraisons")
@CrossOrigin("*")
@RestController
public class LivraisonController {

    @Autowired
    private LivraisonServiceImpl livraisonServiceImpl;

    @GetMapping("/find_all")
    public List<Livraison> findAll() {
        return livraisonServiceImpl.findAll();
    }

    @GetMapping("/find_by_id/{id}")
    public Livraison findById(@PathVariable Integer id) {
        return livraisonServiceImpl.findById(id);
    }

    @PostMapping("/save")
    public Livraison save(@RequestBody Livraison livraison) {
        return livraisonServiceImpl.save(livraison);
    }

    @PutMapping("/update/{id}")
    public Livraison update(@PathVariable Integer id, @RequestBody Livraison livraison) {
        livraison.setIdLivraison(id);
        return livraisonServiceImpl.update(livraison);
    }

    @DeleteMapping("/delete/{id}")
    public Livraison delete(@PathVariable Integer id) {
        Livraison livraison = new Livraison();
        livraison.setIdLivraison(id);
        return livraisonServiceImpl.delete(livraison);
    }

    @GetMapping("/find_by_commande/{idCommande}")
    public Livraison findByCommandeId(@PathVariable Integer idCommande) {
        return livraisonServiceImpl.findByCommandeId(idCommande);
    }

    @GetMapping("/find_by_date/{dateLivraison}")
    public Livraison findByDateLivraison(@PathVariable String dateLivraison) {
        return livraisonServiceImpl.findByDateLivraison(LocalDate.parse(dateLivraison));
    }
}


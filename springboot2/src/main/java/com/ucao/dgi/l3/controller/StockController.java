package com.ucao.dgi.l3.controller;

import com.ucao.dgi.l3.entity.Stock;
import com.ucao.dgi.l3.service.impl.StockServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/stocks")
@CrossOrigin("*")
@RestController
public class StockController {

    @Autowired
    private StockServiceImpl stockServiceImpl;

    @GetMapping("/find_all")
    public List<Stock> findAll() {
        return stockServiceImpl.findAll();
    }

    @GetMapping("/find_by_id/{id}")
    public Stock findById(@PathVariable Integer id) {
        return stockServiceImpl.findById(id);
    }

    @PostMapping("/save")
    public Stock save(@RequestBody Stock stock) {
        return stockServiceImpl.save(stock);
    }

    @PutMapping("/update/{id}")
    public Stock update(@PathVariable Integer id, @RequestBody Stock stock) {
        stock.setIdStock(id);
        return stockServiceImpl.update(stock);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Integer id) {
        Stock stock = new Stock();
        stock.setIdStock(id);
        stockServiceImpl.delete(stock);
    }

    @PostMapping("/update_quantity/{quantite}/{nouvelleQuantite}")
    public Stock updateQuantity(@PathVariable Integer quantite, @PathVariable Integer nouvelleQuantite) {
        return stockServiceImpl.updateQuantity(quantite, nouvelleQuantite);
    }

    @GetMapping("/find_produits_proche_seuil")
    public List<Stock> findProduitProcheDuSeuil() {
        return stockServiceImpl.findProduitProcheDuSeuil();
    }

    @GetMapping("/find_produit_en_rupture")
    public Stock findProduitEnRupture() {
        return stockServiceImpl.findProduitEnRupture();
    }
}


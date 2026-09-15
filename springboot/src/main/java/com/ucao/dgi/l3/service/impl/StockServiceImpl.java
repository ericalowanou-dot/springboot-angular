package com.ucao.dgi.l3.service.impl;

import com.ucao.dgi.l3.entity.Stock;
import com.ucao.dgi.l3.repository.StockRepository;
import com.ucao.dgi.l3.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockServiceImpl implements StockService {

    @Autowired
    private StockRepository stockRepository;

    @Override
    public List<Stock> findAll() {
        return stockRepository.findAll();
    }

    @Override
    public Stock findById(Integer id) {
        return stockRepository.findById(id).orElse(null);
    }

    @Override
    public Stock save(Stock stock) {
        return stockRepository.save(stock);
    }

    @Override
    public Stock update(Stock stock) {
        return stockRepository.save(stock);
    }

    @Override
    public void delete(Stock stock) {
        stockRepository.delete(stock);
    }

    @Override
    public Stock updateQuantity(Integer quantite, Integer nouvelleQuantite) {

        // Recherche du stock via quantité actuelle
        Stock stock = stockRepository.findByQuantite(quantite);

        if (stock == null) return null;

        // Mise à jour
        stock.setQuantite(nouvelleQuantite);

        return stockRepository.save(stock);
    }

    @Override
    public List<Stock> findProduitProcheDuSeuil() {
        return stockRepository.findProduitProcheDuSeuil();
    }

    @Override
    public Stock findProduitEnRupture() {
        return stockRepository.findProduitEnRupture();
    }
}

package com.ucao.dgi.l3.service;

import com.ucao.dgi.l3.entity.Stock;

import java.util.List;

public interface StockService {
    public List<Stock> findAll();

    public Stock findById(Integer id);

    public Stock save(Stock stock);

    public Stock update(Stock stock);

    public void delete(Stock stock);

    public Stock updateQuantity(Integer quantite, Integer nouvelleQuantite);

    public List<Stock> findProduitProcheDuSeuil();

    public Stock findProduitEnRupture();
}

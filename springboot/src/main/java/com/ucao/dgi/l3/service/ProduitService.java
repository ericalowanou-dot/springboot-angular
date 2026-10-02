package com.ucao.dgi.l3.service;

import com.ucao.dgi.l3.dto.CommandeDtos.ProduitRequest;
import com.ucao.dgi.l3.entity.Produit;

import java.util.List;

public interface ProduitService {
    List<Produit> findAll();

    List<Produit> findEnAlerte();

    Produit findById(Integer id);

    Produit save(ProduitRequest requete);

    Produit update(Integer id, ProduitRequest requete);

    Produit ajusterStock(Integer id, int quantite);

    void delete(Integer id);
}

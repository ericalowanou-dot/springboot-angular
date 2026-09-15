package com.ucao.dgi.l3.service;

import com.ucao.dgi.l3.entity.Produit;

import java.util.List;

public interface ProduitService {

    List<Produit> findAll();

    Produit findById(Integer id);

    Produit save(Produit product);

    Produit update(Produit product);

    Produit delete(Produit product);

    // Un fournisseur peut avoir plusieurs produits
    List<Produit> findByFournisseurId(Integer idFournisseur);

    // Plusieurs produits peuvent avoir le même nom
    List<Produit> findByProduitName(String nomProduit);

    // Tous les produits sous le seuil critique
    List<Produit> findProduitsSousSeuil();
}

package com.ucao.dgi.l3.repository;

import com.ucao.dgi.l3.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Integer> {

    // Trouver un stock par quantité exacte
    Stock findByQuantite(Integer quantite);

    // Produits proches du seuil (quantité <= seuil)
    @Query("SELECT s FROM Stock s WHERE s.quantite <= s.produit.seuil AND s.quantite > 0")
    List<Stock> findProduitProcheDuSeuil();

    // Produits en rupture (quantité = 0)
    @Query("SELECT s FROM Stock s WHERE s.quantite = 0")
    Stock findProduitEnRupture();
}

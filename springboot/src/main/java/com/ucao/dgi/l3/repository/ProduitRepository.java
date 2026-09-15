package com.ucao.dgi.l3.repository;

import com.ucao.dgi.l3.entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ProduitRepository extends JpaRepository<Produit, Integer> {

    // ✔ Trouver les produits d’un fournisseur
    List<Produit> findByFournisseurIdFournisseur(Integer idFournisseur);

    // ✔ Trouver par nom (contains, ignore case)
    List<Produit> findByNomContainingIgnoreCase(String nom);

    // ✔ Tous les produits sous le seuil critique
    List<Produit> findBySeuilLessThanEqual(Integer seuil);
}


package com.ucao.dgi.l3.repository;

import com.ucao.dgi.l3.entity.Livraison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface LivraisonRepository extends JpaRepository<Livraison, Integer> {

    Livraison findByCommandeIdCommande(Integer idCommande);

    Livraison findByDateLivraison(LocalDate dateLivraison);
}

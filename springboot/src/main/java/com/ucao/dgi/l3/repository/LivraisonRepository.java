package com.ucao.dgi.l3.repository;

import com.ucao.dgi.l3.entity.Livraison;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LivraisonRepository extends JpaRepository<Livraison, Integer> {

    Livraison findByCommandeIdCommande(Integer idCommande);

    List<Livraison> findAllByDateLivraison(LocalDate dateLivraison);

    List<Livraison> findAllByLivreurIdPersonnel(Integer idPersonnel, Sort sort);
}

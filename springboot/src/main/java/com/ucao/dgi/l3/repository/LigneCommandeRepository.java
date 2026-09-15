package com.ucao.dgi.l3.repository;

import com.ucao.dgi.l3.entity.LigneCommande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LigneCommandeRepository extends JpaRepository<LigneCommande, Integer> {

    List<LigneCommande> findAllByCommandeIdCommande(Integer idCommande);
}

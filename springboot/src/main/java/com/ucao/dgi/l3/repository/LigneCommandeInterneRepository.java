package com.ucao.dgi.l3.repository;

import com.ucao.dgi.l3.entity.LigneCommandeInterne;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LigneCommandeInterneRepository extends JpaRepository<LigneCommandeInterne, Integer> {

    // Récupérer toutes les lignes d'une commande interne
    List<LigneCommandeInterne> findAllByCommandeInterneIdCmdInt(Integer idCmdInt);
}

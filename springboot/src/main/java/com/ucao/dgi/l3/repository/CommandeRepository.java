package com.ucao.dgi.l3.repository;

import com.ucao.dgi.l3.entity.Commande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Integer> {
     List<Commande> findAllByClientIdClient(Integer idClient);

     List<Commande> findAllByEtat(String etat);
}

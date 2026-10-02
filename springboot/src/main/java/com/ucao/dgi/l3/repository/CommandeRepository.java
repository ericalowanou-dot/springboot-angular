package com.ucao.dgi.l3.repository;

import com.ucao.dgi.l3.entity.Commande;
import com.ucao.dgi.l3.entity.StatutCommande;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Integer> {

    List<Commande> findAllByClientIdClient(Integer idClient, Sort sort);

    List<Commande> findAllByStatut(StatutCommande statut, Sort sort);

    List<Commande> findAllByDateCommandeBetween(LocalDate debut, LocalDate fin);

    long countByClientIdClient(Integer idClient);

    Optional<Commande> findByCodeSuivi(String codeSuivi);

    boolean existsByCodeSuivi(String codeSuivi);
}

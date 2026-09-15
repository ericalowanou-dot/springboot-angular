package com.ucao.dgi.l3.repository;

import com.ucao.dgi.l3.entity.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, Integer> {

    Paiement findByCommandeIdCommande(Integer idCommande);

    Paiement findByDatePaiement(LocalDate datePaiement);

    Paiement findByMethode(String methode);
}

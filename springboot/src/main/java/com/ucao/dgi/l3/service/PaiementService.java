package com.ucao.dgi.l3.service;

import com.ucao.dgi.l3.entity.Paiement;

import java.time.LocalDate;
import java.util.List;

public interface PaiementService {
   public List<Paiement>  findAllPaiements();
   public Paiement findPaiementById(Integer idPaiement);
   public Paiement savePaiement(Paiement paiement);
   public Paiement updatePaiement(Paiement paiement);
   public Paiement deletePaiement(Paiement paiement);

   public Paiement findByCommandeId(Integer idCommande);
   public Paiement findByDatePaiement(LocalDate datePaiement);
   public Paiement findByMethode(String methode);
}

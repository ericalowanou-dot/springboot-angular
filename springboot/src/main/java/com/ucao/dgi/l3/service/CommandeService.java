package com.ucao.dgi.l3.service;

import com.ucao.dgi.l3.dto.CommandeDtos.CommandeRequest;
import com.ucao.dgi.l3.entity.Commande;
import com.ucao.dgi.l3.entity.StatutCommande;

import java.util.List;

public interface CommandeService {
    List<Commande> findAll(StatutCommande statut);

    List<Commande> findAllByClient(Integer idClient);

    Commande findById(Integer id);

    Commande creer(CommandeRequest requete);

    Commande modifier(Integer id, CommandeRequest requete);

    Commande changerStatut(Integer id, StatutCommande statut);

    Commande payer(Integer id, String methode);

    void delete(Integer id);
}

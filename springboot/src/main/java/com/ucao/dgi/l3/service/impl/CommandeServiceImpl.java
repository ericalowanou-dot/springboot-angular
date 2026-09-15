package com.ucao.dgi.l3.service.impl;

import com.ucao.dgi.l3.entity.Commande;
import com.ucao.dgi.l3.entity.LigneCommande;
import com.ucao.dgi.l3.repository.CommandeRepository;
import com.ucao.dgi.l3.service.CommandeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service

public class CommandeServiceImpl implements CommandeService {

    @Autowired
    CommandeRepository commandeRepository;

    @Override
    public List<Commande> findAll() {
        return commandeRepository.findAll();
    }

    @Override
    public Commande findById(Integer id) {
        return commandeRepository.findById(id).orElse(null);
    }

    @Override
    public Commande save(Commande commande) {
        return commandeRepository.save(commande);
    }

    @Override
    public Commande delete(Commande idCommande) {
        commandeRepository.delete(idCommande);
        return idCommande;
    }

    @Override
    public Commande update(Commande commande) {
        return commandeRepository.save(commande);
    }

    @Override
    public List<Commande> findAllByClient(Integer idClient) {
        return commandeRepository.findAllByClientIdClient(idClient);
    }

    @Override
    public List<Commande> findByEtat(String etat) {
        return commandeRepository.findAllByEtat(etat);
    }


    @Override
    public Double calculerMontantTotal(Commande commande) {
        double total = 0.0;
        if(commande.getLignes() == null || commande.getLignes().isEmpty()) return 0.0;
        for (LigneCommande ligne : commande.getLignes()){
            double prix = ligne.getPlat().getPrix();
            int quantite = ligne.getQuantite();
            total += prix * quantite;
        }
        return total;
    }

    @Override
    public Commande validerCommande(Commande commande) {
        commande.setEtat("valide");
        return commandeRepository.save(commande);
    }
}

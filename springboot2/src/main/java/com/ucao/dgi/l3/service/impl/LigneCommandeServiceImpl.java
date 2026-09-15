package com.ucao.dgi.l3.service.impl;

import com.ucao.dgi.l3.entity.LigneCommande;
import com.ucao.dgi.l3.repository.LigneCommandeRepository;
import com.ucao.dgi.l3.service.LigneCommandeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LigneCommandeServiceImpl implements LigneCommandeService {

    @Autowired
    private LigneCommandeRepository ligneCommandeRepository;

    @Override
    public List<LigneCommande> findAllByCommande(Integer idCommande) {
        return ligneCommandeRepository.findAllByCommandeIdCommande(idCommande);
    }

    @Override
    public LigneCommande findById(Integer id) {
        return ligneCommandeRepository.findById(id).orElse(null);
    }

    @Override
    public LigneCommande save(LigneCommande ligneCommande) {
        return ligneCommandeRepository.save(ligneCommande);
    }

    @Override
    public LigneCommande update(LigneCommande ligne) {
        return ligneCommandeRepository.save(ligne);
    }

    @Override
    public LigneCommande delete(LigneCommande ligne) {
        ligneCommandeRepository.delete(ligne);
        return ligne;
    }

    @Override
    public LigneCommande calculerSousTotal(Integer idLigne) {

        LigneCommande ligne = ligneCommandeRepository.findById(idLigne).orElse(null);

        if (ligne == null) return null;

        // quantite * prix du produit
        double sousTotal = ligne.getQuantite() * ligne.getPrixUnitaire();

        ligne.setSousTotal(sousTotal);

        return ligneCommandeRepository.save(ligne);
    }
}

package com.ucao.dgi.l3.service.impl;

import com.ucao.dgi.l3.entity.LigneCommandeInterne;
import com.ucao.dgi.l3.repository.LigneCommandeInterneRepository;
import com.ucao.dgi.l3.service.LigneCommandeInterneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LigneCommandeInterneServiceImpl implements LigneCommandeInterneService {

    @Autowired
    private LigneCommandeInterneRepository ligneCommandeInterneRepository;

    @Override
    public List<LigneCommandeInterne> findAllCommandeInterne() {
        return ligneCommandeInterneRepository.findAll();
    }

    @Override
    public LigneCommandeInterne findLigneCommandeInterneById(Integer idLigneCommandeInterne) {
        return ligneCommandeInterneRepository.findById(idLigneCommandeInterne).orElse(null);
    }

    @Override
    public LigneCommandeInterne save(LigneCommandeInterne ligneCommandeInterne) {
        return ligneCommandeInterneRepository.save(ligneCommandeInterne);
    }

    @Override
    public LigneCommandeInterne delete(LigneCommandeInterne ligneCommandeInterne) {
        ligneCommandeInterneRepository.delete(ligneCommandeInterne);
        return ligneCommandeInterneRepository.findById(ligneCommandeInterne.getIdLigneInt()).orElse(null);
    }

    @Override
    public LigneCommandeInterne update(LigneCommandeInterne ligneCommandeInterne) {
        return ligneCommandeInterneRepository.save(ligneCommandeInterne);
    }

    @Override
    public LigneCommandeInterne calculerSousTotal(Integer idLigneCommandeInterne) {

        LigneCommandeInterne ligne = findLigneCommandeInterneById(idLigneCommandeInterne);

        if (ligne == null || ligne.getProduit() == null) {
            return null;
        }

        double prix = ligne.getProduit().getPrixUnitaire();    // prix du produit
        int quantite = ligne.getQuantite();           // quantité

        double sousTotal = prix * quantite;

        ligne.setSousTotal(sousTotal);

        return ligne;
    }
}

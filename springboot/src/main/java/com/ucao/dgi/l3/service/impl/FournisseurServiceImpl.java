package com.ucao.dgi.l3.service.impl;

import com.ucao.dgi.l3.entity.Fournisseur;
import com.ucao.dgi.l3.repository.CommandeInterneRepository;
import com.ucao.dgi.l3.repository.FournisseurRepository;
import com.ucao.dgi.l3.repository.ProduitRepository;
import com.ucao.dgi.l3.service.FournisseurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FournisseurServiceImpl implements FournisseurService {

    @Autowired
    FournisseurRepository fournisseurRepository;
    @Autowired
    CommandeInterneRepository commandeInterneRepository;
    @Autowired
    ProduitRepository produitRepository;

    @Override
    public List<Fournisseur> findAllFournisseur() {
        return fournisseurRepository.findAll();
    }

    @Override
    public Fournisseur findFournisseurById(Integer id) {
        return fournisseurRepository.findById(id).orElse(null);
    }

    @Override
    public Fournisseur findFournisseurByNom(String nom) {
        return fournisseurRepository.findByNom(nom);
    }

    @Override
    public Fournisseur save(Fournisseur fournisseur) {
        return fournisseurRepository.save(fournisseur);
    }

    @Override
    public Fournisseur update(Fournisseur fournisseur) {
        return fournisseurRepository.save(fournisseur);
    }

    @Override
    public Fournisseur delete(Fournisseur idFournisseur) {
        fournisseurRepository.delete(idFournisseur);
        return idFournisseur;
    }

    @Override
    public Fournisseur findProduitByFournisseur(Integer idFournisseur) {
        // Récupère le fournisseur par son ID
        return fournisseurRepository.findById(idFournisseur).orElse(null);
    }

    @Override
    public Fournisseur findCommandeInterne(Integer idFournisseur) {
        // Récupère le fournisseur par son ID
        return fournisseurRepository.findById(idFournisseur).orElse(null);
    }
}

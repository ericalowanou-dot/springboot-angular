package com.ucao.dgi.l3.service;

import com.ucao.dgi.l3.entity.Fournisseur;

import java.util.List;

public interface FournisseurService {
    public List<Fournisseur> findAllFournisseur();
    public Fournisseur findFournisseurById(Integer id);
    public Fournisseur findFournisseurByNom(String nom);
    public Fournisseur save(Fournisseur fournisseur);
    public Fournisseur update(Fournisseur fournisseur);
    public Fournisseur delete(Fournisseur idFournisseur);

    public Fournisseur findProduitByFournisseur(Integer idFournisseur);
    public Fournisseur findCommandeInterne(Integer idFournisseur);
}

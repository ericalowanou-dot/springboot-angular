package com.ucao.dgi.l3.service;

import com.ucao.dgi.l3.entity.Fournisseur;

import java.util.List;

public interface FournisseurService {
    List<Fournisseur> findAll();

    Fournisseur findById(Integer id);

    Fournisseur save(Fournisseur fournisseur);

    Fournisseur update(Integer id, Fournisseur fournisseur);

    void delete(Integer id);
}

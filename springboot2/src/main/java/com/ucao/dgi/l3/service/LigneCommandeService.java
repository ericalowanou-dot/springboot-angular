package com.ucao.dgi.l3.service;

import com.ucao.dgi.l3.entity.LigneCommande;

import java.util.List;

public interface LigneCommandeService {
    public List<LigneCommande> findAllByCommande(Integer idCommande);
    public LigneCommande findById(Integer id);
    public LigneCommande save(LigneCommande ligneCommande);
    public LigneCommande update(LigneCommande ligne);
    public LigneCommande delete(LigneCommande ligne);

    public LigneCommande calculerSousTotal (Integer idLigne);
}

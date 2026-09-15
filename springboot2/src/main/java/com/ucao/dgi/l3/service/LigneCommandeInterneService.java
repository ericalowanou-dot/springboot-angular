package com.ucao.dgi.l3.service;

import com.ucao.dgi.l3.entity.LigneCommandeInterne;

import java.util.List;

public interface LigneCommandeInterneService {
    public List<LigneCommandeInterne> findAllCommandeInterne();
    public LigneCommandeInterne findLigneCommandeInterneById(Integer idLigneCommandeInterne);
    public LigneCommandeInterne save(LigneCommandeInterne ligneCommandeInterne);
    public LigneCommandeInterne delete(LigneCommandeInterne ligneCommandeInterne);
    public LigneCommandeInterne update(LigneCommandeInterne ligneCommandeInterne);

    public LigneCommandeInterne calculerSousTotal(Integer idLigneCommandeInterne);
}

package com.ucao.dgi.l3.service;

import com.ucao.dgi.l3.entity.Livreur;

import java.util.List;

public interface LivreurService {

    List<Livreur> findAllLivreurs();

    Livreur findLivreurById(String id);

    Livreur saveLivreur(Livreur livreur);

    Livreur updateLivreur(Livreur livreur);

    Livreur deleteLivreur(String idLivreur);
}

package com.ucao.dgi.l3.service;

import com.ucao.dgi.l3.dto.CommandeDtos.LivraisonUpdateRequest;
import com.ucao.dgi.l3.entity.Livraison;

import java.util.List;

public interface LivraisonService {
    List<Livraison> findAll();

    Livraison findById(Integer id);

    Livraison mettreAJour(Integer id, LivraisonUpdateRequest requete);
}

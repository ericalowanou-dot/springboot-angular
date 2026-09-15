package com.ucao.dgi.l3.service;

import com.ucao.dgi.l3.entity.Livraison;

import java.time.LocalDate;
import java.util.List;

public interface LivraisonService {
    public List<Livraison> findAll();
    public Livraison findById(Integer id);
    public Livraison save(Livraison livraison);
    public Livraison delete(Livraison livraison);
    public Livraison update(Livraison livraison);

    public Livraison findByCommandeId(Integer idCommande);
    public Livraison findByDateLivraison(LocalDate dateLivraison);
}

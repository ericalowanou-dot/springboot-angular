package com.ucao.dgi.l3.service.impl;

import com.ucao.dgi.l3.entity.Livraison;
import com.ucao.dgi.l3.repository.LivraisonRepository;
import com.ucao.dgi.l3.service.LivraisonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LivraisonServiceImpl implements LivraisonService {

    @Autowired
    private LivraisonRepository livraisonRepository;

    @Override
    public List<Livraison> findAll() {
        return livraisonRepository.findAll();
    }

    @Override
    public Livraison findById(Integer id) {
        return livraisonRepository.findById(id).orElse(null);
    }

    @Override
    public Livraison save(Livraison livraison) {
        return livraisonRepository.save(livraison);
    }

    @Override
    public Livraison delete(Livraison livraison) {
        livraisonRepository.delete(livraison);
        return livraison;
    }

    @Override
    public Livraison update(Livraison livraison) {
        return livraisonRepository.save(livraison);
    }

    @Override
    public Livraison findByCommandeId(Integer idCommande) {
        return livraisonRepository.findByCommandeIdCommande(idCommande);
    }

    @Override
    public Livraison findByDateLivraison(LocalDate dateLivraison) {
        return livraisonRepository.findByDateLivraison(dateLivraison);
    }
}

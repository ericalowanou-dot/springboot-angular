package com.ucao.dgi.l3.service.impl;

import com.ucao.dgi.l3.entity.Produit;
import com.ucao.dgi.l3.repository.ProduitRepository;
import com.ucao.dgi.l3.service.ProduitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProduitServiceImpl implements ProduitService {

    private final ProduitRepository produitRepository;

    @Override
    public List<Produit> findAll() {
        return produitRepository.findAll();
    }

    @Override
    public Produit findById(Integer id) {
        return produitRepository.findById(id)
                .orElse(null);
    }

    @Override
    public Produit save(Produit produit) {
        return produitRepository.save(produit);
    }

    @Override
    public Produit update(Produit produit) {
        if (produit.getIdProduit() == null) {
            throw new IllegalArgumentException("Impossible de mettre à jour : ID Produit manquant !");
        }
        return produitRepository.save(produit);
    }

    @Override
    public Produit delete(Produit produit) {
        if (produit == null || produit.getIdProduit() == null) {
            return null;
        }
        produitRepository.delete(produit);
        return produit;
    }

    @Override
    public List<Produit> findByFournisseurId(Integer idFournisseur) {
        return produitRepository.findByFournisseurIdFournisseur(idFournisseur);
    }

    @Override
    public List<Produit> findByProduitName(String nomProduit) {
        return produitRepository.findByNomContainingIgnoreCase(nomProduit);
    }

    @Override
    public List<Produit> findProduitsSousSeuil() {
        return produitRepository.findBySeuilLessThanEqual(1); // 1 = ruptures / sous seuil
    }
}

package com.ucao.dgi.l3.service.impl;

import com.ucao.dgi.l3.dto.CommandeDtos.ProduitRequest;
import com.ucao.dgi.l3.entity.Fournisseur;
import com.ucao.dgi.l3.entity.Produit;
import com.ucao.dgi.l3.entity.Stock;
import com.ucao.dgi.l3.exception.RessourceIntrouvableException;
import com.ucao.dgi.l3.repository.FournisseurRepository;
import com.ucao.dgi.l3.repository.ProduitRepository;
import com.ucao.dgi.l3.service.ProduitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProduitServiceImpl implements ProduitService {

    private final ProduitRepository produitRepository;
    private final FournisseurRepository fournisseurRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Produit> findAll() {
        return produitRepository.findAll(Sort.by("nom"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Produit> findEnAlerte() {
        return findAll().stream().filter(Produit::isEnAlerte).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Produit findById(Integer id) {
        return produitRepository.findById(id).orElseThrow(() -> new RessourceIntrouvableException("Produit", id));
    }

    @Override
    public Produit save(ProduitRequest requete) {
        Produit produit = new Produit();
        appliquer(produit, requete);
        produit.setStock(new Stock(produit, requete.quantite() == null ? 0 : requete.quantite()));
        return produitRepository.save(produit);
    }

    @Override
    public Produit update(Integer id, ProduitRequest requete) {
        Produit produit = findById(id);
        appliquer(produit, requete);
        if (requete.quantite() != null) {
            ajuster(produit, requete.quantite());
        }
        return produit;
    }

    @Override
    public Produit ajusterStock(Integer id, int quantite) {
        Produit produit = findById(id);
        ajuster(produit, quantite);
        return produit;
    }

    @Override
    public void delete(Integer id) {
        produitRepository.delete(findById(id));
        produitRepository.flush(); // remonte immédiatement une éventuelle violation de clé étrangère
    }

    private void ajuster(Produit produit, int quantite) {
        if (produit.getStock() == null) {
            produit.setStock(new Stock(produit, quantite));
        } else {
            produit.getStock().setQuantite(quantite);
        }
    }

    private void appliquer(Produit produit, ProduitRequest r) {
        produit.setNom(r.nom());
        produit.setPrixUnitaire(r.prixUnitaire());
        produit.setSeuil(r.seuil());
        produit.setUnite(r.unite());
        Fournisseur fournisseur = null;
        if (r.fournisseurId() != null) {
            fournisseur = fournisseurRepository.findById(r.fournisseurId())
                    .orElseThrow(() -> new RessourceIntrouvableException("Fournisseur", r.fournisseurId()));
        }
        produit.setFournisseur(fournisseur);
    }
}

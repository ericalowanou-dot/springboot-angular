package com.ucao.dgi.l3.service;

import com.ucao.dgi.l3.entity.Commande;

import java.util.List;

public interface CommandeService {
    public List<Commande> findAll();
    public Commande findById(Integer id);
    public Commande save(Commande commande);
    public Commande delete(Commande commande);
    public Commande update(Commande commande);

    public List<Commande> findAllByClient(Integer idClient);
    public List<Commande> findByEtat(String etat);
    public Double calculerMontantTotal(Commande commande);
    public Commande validerCommande(Commande commande);
}

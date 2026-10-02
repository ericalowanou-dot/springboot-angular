package com.ucao.dgi.l3.service;

import com.ucao.dgi.l3.dto.CommandeDtos.ApprovisionnementRequest;
import com.ucao.dgi.l3.entity.CommandeInterne;

import java.util.List;

/** Approvisionnements : commandes passées aux fournisseurs, qui alimentent le stock à réception. */
public interface CommandeInterneService {
    List<CommandeInterne> findAll();

    CommandeInterne findById(Integer id);

    CommandeInterne creer(ApprovisionnementRequest requete);

    CommandeInterne recevoir(Integer id);

    CommandeInterne annuler(Integer id);

    void delete(Integer id);
}

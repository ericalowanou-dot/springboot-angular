package com.ucao.dgi.l3.service;

import com.ucao.dgi.l3.entity.CommandeInterne;
import com.ucao.dgi.l3.entity.Fournisseur;

import java.util.List;

public interface CommandeInterneService {
    public List<CommandeInterne> findAllCommandeInterne();
    public CommandeInterne findCommandeInterneById(Integer id);
    public CommandeInterne save(CommandeInterne commandeInterne);
    public CommandeInterne update(CommandeInterne commandeInterne);
    public CommandeInterne delete(CommandeInterne commandeInterne);

    public Fournisseur findFournisseur(Integer idFournisseur);
    public Fournisseur validerCommandeInterne(Integer idCommandeInterne);


}

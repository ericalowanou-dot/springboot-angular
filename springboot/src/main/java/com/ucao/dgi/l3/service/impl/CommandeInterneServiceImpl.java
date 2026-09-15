package com.ucao.dgi.l3.service.impl;

import com.ucao.dgi.l3.entity.CommandeInterne;
import com.ucao.dgi.l3.entity.Fournisseur;
import com.ucao.dgi.l3.repository.CommandeInterneRepository;
import com.ucao.dgi.l3.repository.FournisseurRepository;
import com.ucao.dgi.l3.service.CommandeInterneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommandeInterneServiceImpl implements CommandeInterneService {

    @Autowired
    private CommandeInterneRepository commandeInterneRepository;

    @Autowired
    private FournisseurRepository fournisseurRepository;

    @Override
    public List<CommandeInterne> findAllCommandeInterne() {
        return commandeInterneRepository.findAll();
    }

    @Override
    public CommandeInterne findCommandeInterneById(Integer id) {
        return commandeInterneRepository.findById(id).orElse(null);
    }

    @Override
    public CommandeInterne save(CommandeInterne commandeInterne) {
        return commandeInterneRepository.save(commandeInterne);
    }

    @Override
    public CommandeInterne update(CommandeInterne commandeInterne) {
        return commandeInterneRepository.save(commandeInterne);
    }

    @Override
    public CommandeInterne delete(CommandeInterne commandeInterne) {
        commandeInterneRepository.delete(commandeInterne);
        return commandeInterne;
    }

    @Override
    public Fournisseur findFournisseur(Integer idFournisseur) {
        return fournisseurRepository.findById(idFournisseur).orElse(null);
    }

    @Override
    public Fournisseur validerCommandeInterne(Integer idCommandeInterne) {

        CommandeInterne commande = commandeInterneRepository.findById(idCommandeInterne).orElse(null);
        if (commande == null) return null;

        commande.setEtat("validée");
        commandeInterneRepository.save(commande);

        return commande.getFournisseur();
    }
}

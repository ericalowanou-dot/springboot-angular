package com.ucao.dgi.l3.service.impl;

import com.ucao.dgi.l3.dto.CommandeDtos.ApprovisionnementRequest;
import com.ucao.dgi.l3.dto.CommandeDtos.LigneApproRequest;
import com.ucao.dgi.l3.entity.*;
import com.ucao.dgi.l3.exception.RegleMetierException;
import com.ucao.dgi.l3.exception.RessourceIntrouvableException;
import com.ucao.dgi.l3.repository.CommandeInterneRepository;
import com.ucao.dgi.l3.repository.FournisseurRepository;
import com.ucao.dgi.l3.repository.ProduitRepository;
import com.ucao.dgi.l3.service.CommandeInterneService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CommandeInterneServiceImpl implements CommandeInterneService {

    private final CommandeInterneRepository commandeInterneRepository;
    private final FournisseurRepository fournisseurRepository;
    private final ProduitRepository produitRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CommandeInterne> findAll() {
        return commandeInterneRepository.findAll(Sort.by(Sort.Direction.DESC, "idCmdInt"));
    }

    @Override
    @Transactional(readOnly = true)
    public CommandeInterne findById(Integer id) {
        return commandeInterneRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Approvisionnement", id));
    }

    @Override
    public CommandeInterne creer(ApprovisionnementRequest requete) {
        Fournisseur fournisseur = fournisseurRepository.findById(requete.fournisseurId())
                .orElseThrow(() -> new RessourceIntrouvableException("Fournisseur", requete.fournisseurId()));
        CommandeInterne cmd = new CommandeInterne();
        cmd.setFournisseur(fournisseur);
        cmd.setDate(LocalDate.now());
        cmd.setEtat(CommandeInterne.EN_COURS);

        double total = 0;
        for (LigneApproRequest l : requete.lignes()) {
            Produit produit = produitRepository.findById(l.produitId())
                    .orElseThrow(() -> new RessourceIntrouvableException("Produit", l.produitId()));
            LigneCommandeInterne ligne = new LigneCommandeInterne();
            ligne.setCommandeInterne(cmd);
            ligne.setProduit(produit);
            ligne.setQuantite(l.quantite());
            ligne.setPrixUnitaire(l.prixUnitaire());
            ligne.setSousTotal(l.prixUnitaire() * l.quantite());
            cmd.getLignes().add(ligne);
            total += ligne.getSousTotal();
        }
        cmd.setMontantTotal(total);
        return commandeInterneRepository.save(cmd);
    }

    @Override
    public CommandeInterne recevoir(Integer id) {
        CommandeInterne cmd = enCours(id);
        for (LigneCommandeInterne ligne : cmd.getLignes()) {
            Produit produit = ligne.getProduit();
            if (produit.getStock() == null) {
                produit.setStock(new Stock(produit, 0));
            }
            produit.getStock().setQuantite(produit.getStock().getQuantite() + ligne.getQuantite());
            // le dernier prix d'achat devient le prix de référence du produit
            produit.setPrixUnitaire(ligne.getPrixUnitaire());
        }
        cmd.setEtat(CommandeInterne.RECUE);
        cmd.setDateReception(LocalDate.now());
        return cmd;
    }

    @Override
    public CommandeInterne annuler(Integer id) {
        CommandeInterne cmd = enCours(id);
        cmd.setEtat(CommandeInterne.ANNULEE);
        return cmd;
    }

    @Override
    public void delete(Integer id) {
        CommandeInterne cmd = findById(id);
        if (CommandeInterne.RECUE.equals(cmd.getEtat())) {
            throw new RegleMetierException("Un approvisionnement déjà reçu ne peut pas être supprimé");
        }
        commandeInterneRepository.delete(cmd);
    }

    private CommandeInterne enCours(Integer id) {
        CommandeInterne cmd = findById(id);
        if (!CommandeInterne.EN_COURS.equals(cmd.getEtat())) {
            throw new RegleMetierException("Cet approvisionnement n'est plus en cours");
        }
        return cmd;
    }
}

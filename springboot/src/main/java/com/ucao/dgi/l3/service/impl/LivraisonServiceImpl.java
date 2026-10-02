package com.ucao.dgi.l3.service.impl;

import com.ucao.dgi.l3.dto.CommandeDtos.LivraisonUpdateRequest;
import com.ucao.dgi.l3.entity.*;
import com.ucao.dgi.l3.exception.RegleMetierException;
import com.ucao.dgi.l3.exception.RessourceIntrouvableException;
import com.ucao.dgi.l3.repository.LivraisonRepository;
import com.ucao.dgi.l3.repository.PersonnelRepository;
import com.ucao.dgi.l3.service.LivraisonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class LivraisonServiceImpl implements LivraisonService {

    private final LivraisonRepository livraisonRepository;
    private final PersonnelRepository personnelRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Livraison> findAll() {
        return livraisonRepository.findAll(Sort.by(Sort.Direction.DESC, "idLivraison"));
    }

    @Override
    @Transactional(readOnly = true)
    public Livraison findById(Integer id) {
        return livraisonRepository.findById(id).orElseThrow(() -> new RessourceIntrouvableException("Livraison", id));
    }

    @Override
    public Livraison mettreAJour(Integer id, LivraisonUpdateRequest requete) {
        Livraison livraison = findById(id);
        Commande commande = livraison.getCommande();
        if (commande != null && commande.getStatut() == StatutCommande.ANNULEE) {
            throw new RegleMetierException("La commande associée a été annulée");
        }

        if (requete.livreurId() != null) {
            Personnel livreur = personnelRepository.findById(requete.livreurId())
                    .orElseThrow(() -> new RessourceIntrouvableException("Livreur", requete.livreurId()));
            if (!"LIVREUR".equalsIgnoreCase(livreur.getFonction())) {
                throw new RegleMetierException(livreur.getPrenom() + " " + livreur.getNom() + " n'est pas livreur");
            }
            livraison.setLivreur(livreur);
            if (livraison.getStatut() == null || livraison.getStatut() == StatutLivraison.A_ASSIGNER) {
                livraison.setStatut(StatutLivraison.EN_COURS);
            }
        }

        StatutLivraison statut = requete.statut();
        if (statut != null) {
            if (statut != StatutLivraison.A_ASSIGNER && livraison.getLivreur() == null) {
                throw new RegleMetierException("Assignez d'abord un livreur");
            }
            livraison.setStatut(statut);
            if (statut == StatutLivraison.LIVREE) {
                livraison.setDateLivraison(LocalDate.now());
                if (commande != null) {
                    commande.setStatut(StatutCommande.LIVREE);
                }
            }
        }
        return livraison;
    }
}

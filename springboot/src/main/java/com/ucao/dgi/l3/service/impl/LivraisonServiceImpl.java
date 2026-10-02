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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class LivraisonServiceImpl implements LivraisonService {

    private static final Sort PLUS_RECENTES = Sort.by(Sort.Direction.DESC, "idLivraison");

    private final LivraisonRepository livraisonRepository;
    private final PersonnelRepository personnelRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Livraison> findAll() {
        return livraisonRepository.findAll(PLUS_RECENTES);
    }

    @Override
    @Transactional(readOnly = true)
    public Livraison findById(Integer id) {
        return livraisonRepository.findById(id).orElseThrow(() -> new RessourceIntrouvableException("Livraison", id));
    }

    @Override
    public Livraison mettreAJour(Integer id, LivraisonUpdateRequest requete) {
        Livraison livraison = findById(id);
        verifierCommandeActive(livraison);

        if (requete.livreurId() != null) {
            if (livraison.getStatut() == StatutLivraison.LIVREE) {
                throw new RegleMetierException("Cette livraison est déjà terminée");
            }
            Personnel livreur = personnelRepository.findById(requete.livreurId())
                    .orElseThrow(() -> new RessourceIntrouvableException("Livreur", requete.livreurId()));
            if (!"LIVREUR".equalsIgnoreCase(livreur.getFonction())) {
                throw new RegleMetierException(livreur.getPrenom() + " " + livreur.getNom() + " n'est pas livreur");
            }
            livraison.setLivreur(livreur);
            // nouvelle assignation (ou réassignation après un échec) : le livreur doit repartir
            livraison.setStatut(StatutLivraison.ASSIGNEE);
            livraison.setHeureDepart(null);
            livraison.setHeureFin(null);
            livraison.setDateLivraison(null);
            livraison.setMotifEchec(null);
        }

        StatutLivraison statut = requete.statut();
        if (statut != null && statut != livraison.getStatut()) {
            if (statut != StatutLivraison.A_ASSIGNER && livraison.getLivreur() == null) {
                throw new RegleMetierException("Assignez d'abord un livreur");
            }
            switch (statut) {
                case EN_COURS -> demarrer(livraison);
                case LIVREE -> terminer(livraison, true, null);
                case ECHOUEE -> terminer(livraison, false, requete.motif());
                default -> livraison.setStatut(statut);
            }
        }
        return livraison;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Livraison> mesLivraisons(User livreur) {
        LocalDate aujourdHui = LocalDate.now();
        return livraisonRepository.findAllByLivreurIdPersonnel(personnel(livreur).getIdPersonnel(), PLUS_RECENTES)
                .stream()
                .filter(l -> l.getCommande() == null || l.getCommande().getStatut() != StatutCommande.ANNULEE)
                .filter(l -> l.getStatut() == null || !l.getStatut().estTerminee()
                        || aujourdHui.equals(l.getDateLivraison()))
                .toList();
    }

    @Override
    public Livraison depart(User livreur, Integer id) {
        Livraison livraison = sienne(livreur, id);
        if (livraison.getStatut() != StatutLivraison.ASSIGNEE) {
            throw new RegleMetierException("Cette livraison n'est pas en attente de départ");
        }
        demarrer(livraison);
        return livraison;
    }

    @Override
    public Livraison livree(User livreur, Integer id) {
        Livraison livraison = sienne(livreur, id);
        exigerEnRoute(livraison);
        terminer(livraison, true, null);
        return livraison;
    }

    @Override
    public Livraison echec(User livreur, Integer id, String motif, String commentaire) {
        Livraison livraison = sienne(livreur, id);
        exigerEnRoute(livraison);
        String texte = motif.trim();
        if (commentaire != null && !commentaire.isBlank()) {
            texte += " — " + commentaire.trim();
        }
        terminer(livraison, false, texte);
        return livraison;
    }

    private void demarrer(Livraison livraison) {
        livraison.setStatut(StatutLivraison.EN_COURS);
        livraison.setHeureDepart(LocalDateTime.now());
    }

    private void terminer(Livraison livraison, boolean reussie, String motif) {
        livraison.setStatut(reussie ? StatutLivraison.LIVREE : StatutLivraison.ECHOUEE);
        livraison.setHeureFin(LocalDateTime.now());
        livraison.setDateLivraison(LocalDate.now());
        livraison.setMotifEchec(reussie ? null : motif);
        if (reussie && livraison.getCommande() != null) {
            livraison.getCommande().setStatut(StatutCommande.LIVREE);
        }
    }

    /** Un livreur ne peut agir que sur une livraison qui lui est assignée. */
    private Livraison sienne(User livreur, Integer id) {
        Livraison livraison = findById(id);
        Personnel moi = personnel(livreur);
        if (livraison.getLivreur() == null || !Objects.equals(livraison.getLivreur().getIdPersonnel(), moi.getIdPersonnel())) {
            throw new AccessDeniedException("Cette livraison ne vous est pas assignée");
        }
        verifierCommandeActive(livraison);
        return livraison;
    }

    private void exigerEnRoute(Livraison livraison) {
        if (livraison.getStatut() != StatutLivraison.EN_COURS) {
            throw new RegleMetierException("Indiquez d'abord « Je pars » pour cette livraison");
        }
    }

    private void verifierCommandeActive(Livraison livraison) {
        Commande commande = livraison.getCommande();
        if (commande != null && commande.getStatut() == StatutCommande.ANNULEE) {
            throw new RegleMetierException("La commande associée a été annulée");
        }
    }

    private static Personnel personnel(User livreur) {
        if (livreur.getPersonnel() == null) {
            throw new RegleMetierException("Ce compte n'est rattaché à aucune fiche livreur");
        }
        return livreur.getPersonnel();
    }
}

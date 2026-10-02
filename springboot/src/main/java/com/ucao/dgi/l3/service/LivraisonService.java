package com.ucao.dgi.l3.service;

import com.ucao.dgi.l3.dto.CommandeDtos.LivraisonUpdateRequest;
import com.ucao.dgi.l3.entity.Livraison;
import com.ucao.dgi.l3.entity.User;

import java.util.List;

public interface LivraisonService {
    List<Livraison> findAll();

    Livraison findById(Integer id);

    /** Mise à jour par l'équipe du restaurant (assignation, changement de statut). */
    Livraison mettreAJour(Integer id, LivraisonUpdateRequest requete);

    /** Livraisons du livreur connecté : en attente, en route, et celles terminées aujourd'hui. */
    List<Livraison> mesLivraisons(User livreur);

    /** Courses prêtes, encore libres, que n'importe quel livreur peut prendre. */
    List<Livraison> disponibles();

    /** Le livreur prend une course libre ; échoue si un autre l'a prise avant lui. */
    Livraison prendre(User livreur, Integer id);

    /** Le livreur rend une course qu'il a prise mais pas encore commencée. */
    Livraison liberer(User livreur, Integer id);

    /** « Je pars » : le livreur a récupéré la commande. */
    Livraison depart(User livreur, Integer id);

    Livraison livree(User livreur, Integer id);

    Livraison echec(User livreur, Integer id, String motif, String commentaire);
}

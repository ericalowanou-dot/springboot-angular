package com.ucao.dgi.l3.dto;

import com.ucao.dgi.l3.entity.Commande;
import com.ucao.dgi.l3.entity.Produit;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/** Indicateurs agrégés affichés sur le tableau de bord. */
public record DashboardDto(
        double chiffreAffairesJour,
        double chiffreAffairesMois,
        long commandesJour,
        long commandesEnCours,
        long totalClients,
        long totalPlats,
        double panierMoyen,
        double variationJour,
        List<VenteJour> ventes7Jours,
        List<TopPlat> topPlats,
        Map<String, Long> repartitionStatuts,
        List<Produit> produitsEnAlerte,
        List<Commande> dernieresCommandes) {

    public record VenteJour(LocalDate date, double montant, long commandes) {
    }

    public record TopPlat(Integer idPlat, String nom, String imageUrl, long quantite, double montant) {
    }
}

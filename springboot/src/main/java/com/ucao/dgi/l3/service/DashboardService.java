package com.ucao.dgi.l3.service;

import com.ucao.dgi.l3.dto.DashboardDto;
import com.ucao.dgi.l3.dto.DashboardDto.TopPlat;
import com.ucao.dgi.l3.dto.DashboardDto.VenteJour;
import com.ucao.dgi.l3.entity.*;
import com.ucao.dgi.l3.repository.ClientRepository;
import com.ucao.dgi.l3.repository.CommandeRepository;
import com.ucao.dgi.l3.repository.LivraisonRepository;
import com.ucao.dgi.l3.repository.PlatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/** Calcule les indicateurs du tableau de bord (chiffre d'affaires, ventes, top plats, alertes). */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DashboardService {

    private final CommandeRepository commandeRepository;
    private final ClientRepository clientRepository;
    private final PlatRepository platRepository;
    private final LivraisonRepository livraisonRepository;
    private final ProduitService produitService;

    public DashboardDto calculer() {
        LocalDate aujourdHui = LocalDate.now();
        LocalDate debutMois = aujourdHui.withDayOfMonth(1);
        LocalDate debut = debutMois.isBefore(aujourdHui.minusDays(6)) ? debutMois : aujourdHui.minusDays(6);

        // commandes non annulées depuis le début de la période analysée
        List<Commande> commandes = commandeRepository.findAllByDateCommandeBetween(debut, aujourdHui).stream()
                .filter(c -> c.getStatut() != StatutCommande.ANNULEE)
                .toList();

        Map<LocalDate, List<Commande>> parJour = commandes.stream()
                .collect(Collectors.groupingBy(Commande::getDateCommande));

        List<VenteJour> ventes7Jours = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate jour = aujourdHui.minusDays(i);
            List<Commande> duJour = parJour.getOrDefault(jour, List.of());
            ventes7Jours.add(new VenteJour(jour, total(duJour), duJour.size()));
        }

        List<Commande> duJour = parJour.getOrDefault(aujourdHui, List.of());
        double caJour = total(duJour);
        double caHier = total(parJour.getOrDefault(aujourdHui.minusDays(1), List.of()));
        double variation = caHier == 0 ? 0 : (caJour - caHier) / caHier * 100;
        double caMois = total(commandes.stream().filter(c -> !c.getDateCommande().isBefore(debutMois)).toList());

        List<Commande> toutes = commandeRepository.findAll();
        long enCours = toutes.stream()
                .filter(c -> c.getStatut() != null && !c.getStatut().estTerminee())
                .count();
        Map<String, Long> repartition = new LinkedHashMap<>();
        for (StatutCommande s : StatutCommande.values()) {
            repartition.put(s.name(), toutes.stream().filter(c -> c.getStatut() == s).count());
        }

        List<Commande> valides = toutes.stream().filter(c -> c.getStatut() != StatutCommande.ANNULEE).toList();
        double panierMoyen = valides.isEmpty() ? 0 : total(valides) / valides.size();

        List<Commande> dernieres = commandeRepository
                .findAll(PageRequest.of(0, 6, Sort.by(Sort.Direction.DESC, "idCommande")))
                .getContent();

        List<Livraison> livraisonsJour = livraisonRepository.findAllByDateLivraison(aujourdHui);
        long livrees = livraisonsJour.stream().filter(l -> l.getStatut() == StatutLivraison.LIVREE).count();
        long echouees = livraisonsJour.stream().filter(l -> l.getStatut() == StatutLivraison.ECHOUEE).count();

        return new DashboardDto(caJour, caMois, duJour.size(), enCours, clientRepository.count(),
                platRepository.count(), panierMoyen, variation, livrees, echouees, ventes7Jours, topPlats(valides), repartition,
                produitService.findEnAlerte(), dernieres);
    }

    private List<TopPlat> topPlats(List<Commande> commandes) {
        Map<Integer, TopPlat> cumul = new HashMap<>();
        for (Commande c : commandes) {
            for (LigneCommande l : c.getLignes()) {
                Plat p = l.getPlat();
                if (p == null) {
                    continue;
                }
                double montant = l.getSousTotal() == null ? 0 : l.getSousTotal();
                cumul.merge(p.getIdPlat(),
                        new TopPlat(p.getIdPlat(), p.getNom(), p.getImageUrl(), l.getQuantite(), montant),
                        (a, b) -> new TopPlat(a.idPlat(), a.nom(), a.imageUrl(),
                                a.quantite() + b.quantite(), a.montant() + b.montant()));
            }
        }
        return cumul.values().stream()
                .sorted(Comparator.comparingLong(TopPlat::quantite).reversed())
                .limit(5)
                .toList();
    }

    private static double total(List<Commande> commandes) {
        return commandes.stream().mapToDouble(c -> c.getMontantTotal() == null ? 0 : c.getMontantTotal()).sum();
    }
}

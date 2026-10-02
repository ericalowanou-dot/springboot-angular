package com.ucao.dgi.l3.dto;

import com.ucao.dgi.l3.dto.CommandeDtos.LigneRequest;
import com.ucao.dgi.l3.entity.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;

/** DTO de la commande en ligne passée par un client depuis la carte publique. */
public final class EnLigneDtos {

    private EnLigneDtos() {
    }

    public record CommandeEnLigneRequest(
            @NotNull(message = "Choisissez à emporter ou en livraison") TypeCommande type,
            @NotBlank(message = "Votre nom est obligatoire") @Size(min = 2, max = 60) String nom,
            @NotBlank(message = "Votre téléphone est obligatoire")
            @Pattern(regexp = "^\\+?[0-9 .\\-]{8,20}$", message = "Numéro de téléphone invalide") String telephone,
            @Size(max = 255) String adresse,
            @Size(max = 255) String notes,
            @NotEmpty(message = "Votre panier est vide") @Size(max = 30, message = "Trop d'articles différents")
            List<@Valid LigneRequest> lignes) {
    }

    public record CommandeEnLigneResponse(String codeSuivi, Integer numero, double montantTotal) {
    }

    /** Vue publique d'une commande : aucune donnée personnelle n'y figure. */
    public record SuiviCommande(String codeSuivi, Integer numero, StatutCommande statut, TypeCommande type,
                                LocalDateTime creeLe, double montantTotal, boolean payee,
                                List<LigneSuivi> lignes, StatutLivraison statutLivraison,
                                LocalDateTime heureDepart, LocalDateTime heureFin) {

        public static SuiviCommande of(Commande c) {
            Livraison l = c.getLivraison();
            return new SuiviCommande(c.getCodeSuivi(), c.getIdCommande(), c.getStatut(), c.getType(), c.getCreeLe(),
                    c.getMontantTotal() == null ? 0 : c.getMontantTotal(), c.isPayee(),
                    c.getLignes().stream()
                            .map(lg -> new LigneSuivi(lg.getLibelle(), lg.getMenu() != null,
                                    lg.getQuantite(), lg.getSousTotal() == null ? 0 : lg.getSousTotal()))
                            .toList(),
                    l == null ? null : l.getStatut(), l == null ? null : l.getHeureDepart(),
                    l == null ? null : l.getHeureFin());
        }
    }

    public record LigneSuivi(String plat, boolean formule, int quantite, double sousTotal) {
    }
}

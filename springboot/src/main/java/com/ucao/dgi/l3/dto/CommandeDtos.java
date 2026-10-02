package com.ucao.dgi.l3.dto;

import com.ucao.dgi.l3.entity.StatutCommande;
import com.ucao.dgi.l3.entity.StatutLivraison;
import com.ucao.dgi.l3.entity.TypeCommande;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

/** DTO d'entrée pour les commandes clients, paiements, livraisons et approvisionnements. */
public final class CommandeDtos {

    private CommandeDtos() {
    }

    public record LigneRequest(
            @NotNull(message = "Le plat est obligatoire") Integer platId,
            @NotNull @Min(value = 1, message = "La quantité doit être au moins 1") Integer quantite) {
    }

    public record CommandeRequest(
            Integer clientId,
            TypeCommande type,
            Integer numeroTable,
            @Size(max = 255) String notes,
            @Size(max = 255) String adresseLivraison,
            @NotEmpty(message = "La commande doit contenir au moins un plat") List<@Valid LigneRequest> lignes) {
    }

    public record StatutRequest(@NotNull(message = "Le statut est obligatoire") StatutCommande statut) {
    }

    public record PaiementRequest(@NotBlank(message = "Le moyen de paiement est obligatoire") String methode) {
    }

    public record LivraisonUpdateRequest(Integer livreurId, StatutLivraison statut, @Size(max = 255) String motif) {
    }

    /** Déclaration d'échec par le livreur : motif obligatoire, commentaire libre. */
    public record EchecRequest(
            @NotBlank(message = "Indiquez le motif de l'échec") @Size(max = 120) String motif,
            @Size(max = 130) String commentaire) {
    }

    public record LigneApproRequest(
            @NotNull(message = "Le produit est obligatoire") Integer produitId,
            @NotNull @Min(value = 1, message = "La quantité doit être au moins 1") Integer quantite,
            @NotNull @PositiveOrZero Double prixUnitaire) {
    }

    public record ApprovisionnementRequest(
            @NotNull(message = "Le fournisseur est obligatoire") Integer fournisseurId,
            @NotEmpty(message = "Ajoutez au moins un produit") List<@Valid LigneApproRequest> lignes) {
    }

    public record ProduitRequest(
            @NotBlank(message = "Le nom du produit est obligatoire") @Size(max = 80) String nom,
            @NotNull(message = "Le prix est obligatoire") @PositiveOrZero Double prixUnitaire,
            @NotNull(message = "Le seuil est obligatoire") @PositiveOrZero Integer seuil,
            @Size(max = 20) String unite,
            Integer fournisseurId,
            @PositiveOrZero Integer quantite) {
    }

    public record AjustementStockRequest(@NotNull @PositiveOrZero(message = "La quantité doit être positive") Integer quantite) {
    }
}

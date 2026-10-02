package com.ucao.dgi.l3.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "ligne_commande")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class LigneCommande implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idLigne;

    private Integer quantite;

    /** Prix figé au moment de la commande (le prix du plat peut changer ensuite). */
    private double prixUnitaire;

    // 🔹 Chaque ligne appartient à une commande
    @ManyToOne
    @JoinColumn(name = "commande_id")
    @JsonIgnore
    private Commande commande;

    // 🔹 Chaque ligne concerne un seul plat
    @ManyToOne
    @JoinColumn(name = "plat_id")
    @JsonIgnoreProperties({"lignes", "categorie"})
    private Plat plat;

    // 🔹 ... ou une formule (menu), facturée au prix de la formule
    @ManyToOne
    @JoinColumn(name = "menu_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Menu menu;

    @ManyToOne
    @JoinColumn(name = "panier_id")
    @JsonIgnore
    private Panier panier;

    private Double sousTotal;

    public LigneCommande(Plat plat, int quantite) {
        this.plat = plat;
        this.quantite = quantite;
        this.prixUnitaire = plat.getPrix();
        this.sousTotal = plat.getPrix() * quantite;
    }

    public LigneCommande(Menu menu, int quantite) {
        this.menu = menu;
        this.quantite = quantite;
        this.prixUnitaire = menu.getPrix();
        this.sousTotal = menu.getPrix() * quantite;
    }

    /** Nom affiché sur le ticket : le plat, ou la formule. */
    public String getLibelle() {
        if (menu != null) {
            return menu.getNom();
        }
        return plat == null ? "Article" : plat.getNom();
    }
}

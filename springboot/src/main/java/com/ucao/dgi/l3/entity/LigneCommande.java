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
}

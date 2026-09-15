package com.ucao.dgi.l3.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "ligne_commande")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class LigneCommande implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idLigne;

    private Integer quantite;

    private double prixUnitaire;

    // 🔹 Chaque ligne appartient à une commande
    @ManyToOne
    @JoinColumn(name = "commande_id")
    @JsonIgnoreProperties({"lignes", "paiement", "livraison", "client"})
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
}

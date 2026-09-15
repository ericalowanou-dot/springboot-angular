package com.ucao.dgi.l3.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "ligne_commande_interne")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class LigneCommandeInterne implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idLigneInt;

    private Integer quantite;

    // 🔹 Chaque ligne appartient à une commande interne
    @ManyToOne
    @JoinColumn(name = "commande_interne_id")
    @JsonIgnoreProperties({"lignes", "fournisseur"})
    private CommandeInterne commandeInterne;

    // 🔹 Et concerne un produit
    @ManyToOne
    @JoinColumn(name = "produit_id")
    @JsonIgnoreProperties({"stock", "fournisseur"})
    private Produit produit;

    private Double sousTotal;
}

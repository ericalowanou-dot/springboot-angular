package com.ucao.dgi.l3.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/** Matière première ou fourniture du restaurant, dont la quantité est suivie dans {@link Stock}. */
@Entity
@Table(name = "produit")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Produit implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProduit;

    private String nom;

    private Double prixUnitaire;

    /** Quantité minimale en dessous de laquelle le produit est en alerte. */
    private Integer seuil;

    /** Unité de mesure (kg, L, pièce...). */
    @Column(length = 20)
    private String unite;

    // 🔹 Chaque produit a un stock
    @OneToOne(mappedBy = "produit", cascade = CascadeType.ALL)
    @JsonIgnore
    private Stock stock;

    // 🔹 Fournisseur du produit
    @ManyToOne
    @JoinColumn(name = "fournisseur_id")
    @JsonIgnoreProperties({"produits", "commandesInternes"})
    private Fournisseur fournisseur;

    public int getQuantiteStock() {
        return stock == null ? 0 : stock.getQuantite();
    }

    public boolean isEnAlerte() {
        return getQuantiteStock() <= (seuil == null ? 0 : seuil);
    }
}

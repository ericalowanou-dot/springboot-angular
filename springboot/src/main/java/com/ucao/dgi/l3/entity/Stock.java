package com.ucao.dgi.l3.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "stock")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idStock;

    private int quantite;

    // 🔹 Chaque stock correspond à un produit
    @OneToOne
    @JoinColumn(name = "produit_id")
    @JsonIgnoreProperties({"stock", "fournisseur"})
    private Produit produit;

    public Stock(Produit produit, int quantite) {
        this.produit = produit;
        this.quantite = quantite;
    }
}

package com.ucao.dgi.l3.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "produit")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Produit implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProduit;

    private String nom;
    private Double prixUnitaire;
    private Integer seuil;

    // 🔹 Chaque produit a un stock
    @OneToOne(mappedBy = "produit", cascade = CascadeType.ALL)
    @JsonIgnore
    private Stock stock;

    // 🔹 Fournisseur du produit
    @ManyToOne
    @JoinColumn(name = "fournisseur_id")
    @JsonIgnoreProperties({"produits", "commandesInternes"})
    private Fournisseur fournisseur;
}

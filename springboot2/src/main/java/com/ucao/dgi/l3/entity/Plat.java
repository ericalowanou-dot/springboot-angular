package com.ucao.dgi.l3.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "plat")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Plat implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPlat;

    @Column(nullable = false, length = 50)
    private String nom;

    @Column(nullable = false)
    private Double prix;

    @Column(length = 255)
    private String description;

    @Column(name = "image_url", length = 500)
    private String imageUrl; // URL de l'image du plat

    // 🔹 Un plat appartient à une catégorie
    @ManyToOne
    @JoinColumn(name = "categorie_id")
    @JsonIgnoreProperties({"plats"})
    private CategoriePlat categorie;

    // 🔹 Un plat peut apparaître dans plusieurs lignes de commande
    @OneToMany(mappedBy = "plat")
    @JsonIgnore
    private List<LigneCommande> lignes;
}

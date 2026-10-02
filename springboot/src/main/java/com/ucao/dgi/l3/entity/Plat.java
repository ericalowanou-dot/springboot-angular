package com.ucao.dgi.l3.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "plat")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Plat implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPlat;

    @NotBlank(message = "Le nom du plat est obligatoire")
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String nom;

    @NotNull(message = "Le prix est obligatoire")
    @PositiveOrZero(message = "Le prix doit être positif")
    @Column(nullable = false)
    private Double prix;

    @Size(max = 255)
    @Column(length = 255)
    private String description;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    /** Un plat indisponible n'est plus proposé à la commande. */
    private Boolean disponible = true;

    // 🔹 Un plat appartient à une catégorie
    @ManyToOne
    @JoinColumn(name = "categorie_id")
    @JsonIgnoreProperties({"plats", "nombrePlats"})
    private CategoriePlat categorie;

    // 🔹 Un plat peut apparaître dans plusieurs lignes de commande
    @OneToMany(mappedBy = "plat")
    @JsonIgnore
    private List<LigneCommande> lignes;

    public Plat(String nom, Double prix, String description, CategoriePlat categorie) {
        this.nom = nom;
        this.prix = prix;
        this.description = description;
        this.categorie = categorie;
    }

    @JsonIgnore
    public boolean isCommandable() {
        return disponible == null || disponible;
    }
}

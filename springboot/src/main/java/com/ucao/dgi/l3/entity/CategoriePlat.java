package com.ucao.dgi.l3.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "categorie_plat")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CategoriePlat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCategorie;

    @NotBlank(message = "Le nom de la catégorie est obligatoire")
    @Size(max = 60)
    private String nom;

    @Size(max = 255)
    private String description;

    // 🔹 Une catégorie contient plusieurs plats
    @OneToMany(mappedBy = "categorie")
    @JsonIgnore
    private List<Plat> plats;

    public CategoriePlat(String nom, String description) {
        this.nom = nom;
        this.description = description;
    }

    /** Nombre de plats rattachés, exposé au frontend. */
    public int getNombrePlats() {
        return plats == null ? 0 : plats.size();
    }
}

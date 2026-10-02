package com.ucao.dgi.l3.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "menu")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Menu implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMenu;

    @NotBlank(message = "Le nom du menu est obligatoire")
    @Size(max = 80)
    private String nom;

    @Size(max = 255)
    private String description;

    @PositiveOrZero(message = "Le prix doit être positif")
    private double prix;

    // 🔹 Un menu contient plusieurs plats
    @ManyToMany
    @JoinTable(
            name = "menu_plat",
            joinColumns = @JoinColumn(name = "menu_id"),
            inverseJoinColumns = @JoinColumn(name = "plat_id")
    )
    @JsonIgnoreProperties({"categorie", "lignes"})
    private List<Plat> plats = new ArrayList<>();

    /** Une formule se commande si elle contient des plats et qu'ils sont tous disponibles. */
    public boolean isCommandable() {
        return plats != null && !plats.isEmpty() && plats.stream().allMatch(Plat::isCommandable);
    }

    /** Somme des prix des plats pris séparément (pour afficher l'économie réalisée). */
    public double getPrixSepare() {
        return plats == null ? 0 : plats.stream().mapToDouble(p -> p.getPrix() == null ? 0 : p.getPrix()).sum();
    }
}

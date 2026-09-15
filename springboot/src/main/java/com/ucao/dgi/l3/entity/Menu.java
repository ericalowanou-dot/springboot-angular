package com.ucao.dgi.l3.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "menu")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Menu implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMenu;

    private String nom;
    private String description;
    private double prix;

    // 🔹 Un menu contient plusieurs plats
    @ManyToMany
    @JoinTable(
            name = "menu_plat",
            joinColumns = @JoinColumn(name = "menu_id"),
            inverseJoinColumns = @JoinColumn(name = "plat_id")
    )
    @JsonIgnoreProperties({"categorie", "lignes"})
    private List<Plat> plats;
}

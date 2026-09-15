package com.ucao.dgi.l3.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "categorie_plat")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CategoriePlat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCategorie;

    private String nom;

    // 🔹 Une catégorie contient plusieurs plats
    @OneToMany(mappedBy = "categorie")
    @JsonIgnore
    private List<Plat> plats;
}

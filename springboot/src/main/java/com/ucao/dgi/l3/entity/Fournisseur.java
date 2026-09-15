package com.ucao.dgi.l3.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "fournisseur")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Fournisseur implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idFournisseur;

    private String nom;
    private String contact;

    // 🔹 Un fournisseur fournit plusieurs produits
    @OneToMany(mappedBy = "fournisseur")
    @JsonIgnore
    private List<Produit> produits;

    // 🔹 Et reçoit plusieurs commandes internes
    @OneToMany(mappedBy = "fournisseur")
    @JsonIgnore
    private List<CommandeInterne> commandesInternes;
}

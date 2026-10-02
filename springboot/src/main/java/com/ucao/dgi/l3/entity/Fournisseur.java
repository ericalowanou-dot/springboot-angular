package com.ucao.dgi.l3.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "fournisseur")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Fournisseur implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idFournisseur;

    @NotBlank(message = "Le nom du fournisseur est obligatoire")
    @Size(max = 80)
    private String nom;

    /** Nom de la personne à contacter. */
    @Size(max = 80)
    private String contact;

    @Size(max = 30)
    private String telephone;

    @Size(max = 100)
    private String email;

    @Size(max = 150)
    private String adresse;

    // 🔹 Un fournisseur fournit plusieurs produits
    @OneToMany(mappedBy = "fournisseur")
    @JsonIgnore
    private List<Produit> produits;

    // 🔹 Et reçoit plusieurs commandes internes
    @OneToMany(mappedBy = "fournisseur")
    @JsonIgnore
    private List<CommandeInterne> commandesInternes;

    public Fournisseur(String nom, String contact, String telephone, String email, String adresse) {
        this.nom = nom;
        this.contact = contact;
        this.telephone = telephone;
        this.email = email;
        this.adresse = adresse;
    }
}

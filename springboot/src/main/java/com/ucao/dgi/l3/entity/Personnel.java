package com.ucao.dgi.l3.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "personnel")
@Inheritance(strategy = InheritanceType.JOINED) // conservé pour la compatibilité avec le schéma existant
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Personnel {

    /** Fonctions reconnues par l'application. */
    public static final Set<String> FONCTIONS = Set.of("CHEF", "CUISINIER", "SERVEUR", "LIVREUR", "CAISSIER", "GERANT");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPersonnel;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 50)
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 50)
    private String prenom;

    @NotBlank(message = "La fonction est obligatoire")
    private String fonction;

    @Size(max = 30)
    private String telephone;

    @Size(max = 100)
    private String email;

    private Double salaire;

    private LocalDate dateEmbauche;

    private Boolean actif = true;

    public Personnel(String nom, String prenom, String fonction, String telephone, Double salaire) {
        this.nom = nom;
        this.prenom = prenom;
        this.fonction = fonction;
        this.telephone = telephone;
        this.salaire = salaire;
        this.dateEmbauche = LocalDate.now().minusMonths(6);
    }
}

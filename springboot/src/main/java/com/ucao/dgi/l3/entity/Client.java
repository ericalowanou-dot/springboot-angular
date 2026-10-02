package com.ucao.dgi.l3.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "rad_client", uniqueConstraints = @UniqueConstraint(columnNames = { "nom", "prenom", "telephone" }))
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Client implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_client_generator")
    @SequenceGenerator(name = "seq_client_generator", sequenceName = "client_id_seq", initialValue = 1, allocationSize = 1)
    @Column(name = "id_client")
    private Integer idClient;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 30)
    @Column(name = "nom", length = 30, nullable = false)
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 30)
    @Column(name = "prenom", length = 30, nullable = false)
    private String prenom;

    @NotBlank(message = "L'adresse est obligatoire")
    @Size(max = 30, message = "L'adresse ne doit pas dépasser 30 caractères")
    @Column(name = "address", length = 30, nullable = false)
    private String address;

    @NotBlank(message = "Le téléphone est obligatoire")
    @Size(max = 30)
    @Column(name = "telephone", length = 30, nullable = false)
    private String telephone;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Email invalide")
    @Size(max = 30, message = "L'email ne doit pas dépasser 30 caractères")
    @Column(name = "email", length = 30, nullable = false)
    private String email;

    // 🔹 Un client peut passer plusieurs commandes (pas de cascade : on ne supprime jamais l'historique)
    @OneToMany(mappedBy = "client")
    @JsonIgnore
    private List<Commande> commandes;

    // 🔹 Un client possède un panier
    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL)
    @JsonIgnore
    private Panier panier;

    public Client(String nom, String prenom, String address, String telephone, String email) {
        this.nom = nom;
        this.prenom = prenom;
        this.address = address;
        this.telephone = telephone;
        this.email = email;
    }
}

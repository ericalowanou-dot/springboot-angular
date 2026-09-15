package com.ucao.dgi.l3.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "rad_client", uniqueConstraints = @UniqueConstraint(columnNames = { "nom", "prenom", "telephone" }))
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })

public class Client implements Serializable { // permet à Hibernate ou à Spring de manipuler l’objet sans problème.

    @Id

    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_client_generator")
    @SequenceGenerator(name = "seq_client_generator", sequenceName = "client_id_seq", initialValue = 1, allocationSize = 1)

    @Column(name = "id_client")
    private Integer idClient;

    @Column(name = "nom", length = 30, nullable = false)
    private String nom;

    @Column(name = "prenom", length = 30, nullable = false)
    private String prenom;

    @Column(name = "address", length = 30, nullable = false)
    private String address;

    @Column(name = "telephone", length = 30, nullable = false)
    private String telephone;

    @Column(name = "email", length = 30, nullable = false)
    private String email;

    // 🔹 Un client peut passer plusieurs commandes
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Commande> commandes;

    // 🔹 Un client possède un panier
    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL)
    @JsonIgnore
    private Panier panier;
}

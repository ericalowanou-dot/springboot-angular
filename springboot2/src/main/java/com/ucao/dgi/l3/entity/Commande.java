package com.ucao.dgi.l3.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
@Entity
@Table(name = "rad_commande")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Commande implements Serializable { //Elle permet de sauvegarder, transférer ou mettre en cache des objets Java.
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "seq_commande", sequenceName = "commande_id_seq", initialValue = 1, allocationSize = 1)
    private Integer idCommande;

    private LocalDate dateCommande;

    private String etat; // Ex: "en préparation", "livrée"

    // Plusieurs commandes appartiennent à un seul client
    @ManyToOne
    @JoinColumn(name = "id_client")
    @JsonIgnoreProperties({"commandes", "panier"})
    private Client client;

    // Une commande contient plusieurs lignes
    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<LigneCommande> lignes;

    // Chaque commande a un paiement
    @OneToOne(mappedBy = "commande", cascade = CascadeType.ALL)
    @JsonIgnore
    private Paiement paiement;

    // Une commande peut avoir une livraison
    @OneToOne(mappedBy = "commande", cascade = CascadeType.ALL)
    @JsonIgnore
    private Livraison livraison;
}

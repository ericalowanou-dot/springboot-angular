package com.ucao.dgi.l3.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "commande_interne")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CommandeInterne implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCmdInt;

    private LocalDate date;
    private String etat;

    // 🔹 Fournisseur concerné
    @ManyToOne
    @JoinColumn(name = "fournisseur_id")
    @JsonIgnoreProperties({"produits", "commandesInternes"})
    private Fournisseur fournisseur;

    // 🔹 Lignes associées
    @OneToMany(mappedBy = "commandeInterne", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<LigneCommandeInterne> lignes;
}

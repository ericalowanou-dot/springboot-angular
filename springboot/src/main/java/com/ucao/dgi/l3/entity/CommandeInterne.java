package com.ucao.dgi.l3.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Commande d'approvisionnement passée à un fournisseur. */
@Entity
@Table(name = "commande_interne")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CommandeInterne implements Serializable {

    public static final String EN_COURS = "EN_COURS";
    public static final String RECUE = "RECUE";
    public static final String ANNULEE = "ANNULEE";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCmdInt;

    private LocalDate date;

    private LocalDate dateReception;

    /** EN_COURS, RECUE ou ANNULEE. */
    private String etat;

    private Double montantTotal;

    // 🔹 Fournisseur concerné
    @ManyToOne
    @JoinColumn(name = "fournisseur_id")
    @JsonIgnoreProperties({"produits", "commandesInternes"})
    private Fournisseur fournisseur;

    // 🔹 Lignes associées
    @OneToMany(mappedBy = "commandeInterne", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"commandeInterne"})
    private List<LigneCommandeInterne> lignes = new ArrayList<>();
}

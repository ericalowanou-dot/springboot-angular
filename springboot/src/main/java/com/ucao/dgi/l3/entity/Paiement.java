package com.ucao.dgi.l3.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "paiement")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Paiement {

    /** Moyens de paiement acceptés (stockés en texte dans la colonne "methode"). */
    public static final Set<String> METHODES = Set.of("ESPECES", "CARTE", "MOBILE_MONEY");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPaiement;

    private LocalDate datePaiement;

    private double montant;

    private String methode;

    // 🔹 Lié à une commande
    @OneToOne
    @JoinColumn(name = "commande_id")
    @JsonIgnoreProperties({"paiement", "livraison", "lignes"})
    private Commande commande;
}

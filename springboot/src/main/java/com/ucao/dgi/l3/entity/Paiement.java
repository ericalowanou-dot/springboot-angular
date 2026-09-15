package com.ucao.dgi.l3.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "paiement")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPaiement;

    private LocalDate datePaiement;
    private double montant;
    private String methode;

    // 🔹 Lié à une commande
    @OneToOne
    @JoinColumn(name = "commande_id")
    @JsonIgnoreProperties({"paiement", "livraison", "lignes", "client"})
    private Commande commande;
}

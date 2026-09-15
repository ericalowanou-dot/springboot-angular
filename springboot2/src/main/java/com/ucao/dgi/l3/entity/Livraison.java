package com.ucao.dgi.l3.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "livraison")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Livraison implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idLivraison;

    private LocalDate dateLivraison;
    private String adresseDestination;

    // 🔹 Liée à une commande
    @OneToOne
    @JoinColumn(name = "commande_id")
    @JsonIgnoreProperties({"paiement", "livraison", "lignes", "client"})
    private Commande commande;
}

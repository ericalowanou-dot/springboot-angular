package com.ucao.dgi.l3.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "panier")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Panier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPanier;

    private LocalDate dateCreation;

    // 🔹 Chaque panier appartient à un seul client
    @OneToOne
    @JoinColumn(name = "client_id")
    private Client client;

    // 🔹 Un panier contient plusieurs lignes
    @OneToMany(mappedBy = "panier", cascade = CascadeType.ALL)
    private List<LigneCommande> lignes;
}

package com.ucao.dgi.l3.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "livraison")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Livraison implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idLivraison;

    /** Date effective de livraison (renseignée quand la livraison est terminée). */
    private LocalDate dateLivraison;

    private String adresseDestination;

    /** Moment où le livreur a indiqué « Je pars ». */
    private LocalDateTime heureDepart;

    /** Moment où la livraison a été marquée livrée ou échouée. */
    private LocalDateTime heureFin;

    /** Raison de l'échec, saisie par le livreur ou l'équipe. */
    @Column(length = 255)
    private String motifEchec;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private StatutLivraison statut;

    @ManyToOne
    @JoinColumn(name = "livreur_id")
    private Personnel livreur;

    // 🔹 Liée à une commande
    @OneToOne
    @JoinColumn(name = "commande_id")
    @JsonIgnoreProperties({"livraison"})
    private Commande commande;
}

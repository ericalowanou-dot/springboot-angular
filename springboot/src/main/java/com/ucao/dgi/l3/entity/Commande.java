package com.ucao.dgi.l3.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rad_commande")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Commande implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idCommande;

    private LocalDate dateCommande;

    private LocalDateTime creeLe;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private StatutCommande statut;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TypeCommande type;

    private Integer numeroTable;

    @Column(length = 255)
    private String notes;

    /** Calculé côté serveur à partir des lignes, jamais fourni par le client HTTP. */
    private Double montantTotal;

    /** Commande passée par le client lui-même depuis la carte en ligne. */
    private Boolean enLigne = false;

    /** Code communiqué au client pour suivre sa commande en ligne (ex. K7M3Q9TX). */
    @Column(length = 12, unique = true)
    private String codeSuivi;

    /** Coordonnées saisies en ligne (le client n'a pas forcément de fiche). */
    @Column(length = 60)
    private String nomContact;

    @Column(length = 30)
    private String telephoneContact;

    // Plusieurs commandes appartiennent à un seul client (optionnel pour une commande au comptoir)
    @ManyToOne
    @JoinColumn(name = "id_client")
    @JsonIgnoreProperties({"commandes", "panier"})
    private Client client;

    // Une commande contient plusieurs lignes
    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"commande", "panier"})
    private List<LigneCommande> lignes = new ArrayList<>();

    // Chaque commande a au plus un paiement
    @OneToOne(mappedBy = "commande", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"commande"})
    private Paiement paiement;

    // Une commande en livraison a une livraison
    @OneToOne(mappedBy = "commande", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"commande"})
    private Livraison livraison;

    public boolean isPayee() {
        return paiement != null;
    }

    public void ajouterLigne(LigneCommande ligne) {
        ligne.setCommande(this);
        lignes.add(ligne);
    }

    public void recalculerTotal() {
        this.montantTotal = lignes.stream()
                .mapToDouble(l -> l.getSousTotal() == null ? 0 : l.getSousTotal())
                .sum();
    }
}

package com.ucao.dgi.l3.service.impl;

import com.ucao.dgi.l3.dto.CommandeDtos.CommandeRequest;
import com.ucao.dgi.l3.dto.CommandeDtos.LigneRequest;
import com.ucao.dgi.l3.entity.*;
import com.ucao.dgi.l3.exception.RegleMetierException;
import com.ucao.dgi.l3.exception.RessourceIntrouvableException;
import com.ucao.dgi.l3.repository.ClientRepository;
import com.ucao.dgi.l3.repository.CommandeRepository;
import com.ucao.dgi.l3.repository.LivraisonRepository;
import com.ucao.dgi.l3.repository.PlatRepository;
import com.ucao.dgi.l3.service.CommandeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CommandeServiceImpl implements CommandeService {

    private static final Sort PLUS_RECENTES = Sort.by(Sort.Direction.DESC, "idCommande");

    private final CommandeRepository commandeRepository;
    private final ClientRepository clientRepository;
    private final PlatRepository platRepository;
    private final LivraisonRepository livraisonRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Commande> findAll(StatutCommande statut) {
        return statut == null
                ? commandeRepository.findAll(PLUS_RECENTES)
                : commandeRepository.findAllByStatut(statut, PLUS_RECENTES);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Commande> findAllByClient(Integer idClient) {
        return commandeRepository.findAllByClientIdClient(idClient, PLUS_RECENTES);
    }

    @Override
    @Transactional(readOnly = true)
    public Commande findById(Integer id) {
        return commandeRepository.findById(id).orElseThrow(() -> new RessourceIntrouvableException("Commande", id));
    }

    @Override
    public Commande creer(CommandeRequest requete) {
        Commande commande = new Commande();
        commande.setDateCommande(LocalDate.now());
        commande.setCreeLe(LocalDateTime.now());
        commande.setStatut(StatutCommande.EN_ATTENTE);
        appliquer(commande, requete);
        return commandeRepository.save(commande);
    }

    @Override
    public Commande modifier(Integer id, CommandeRequest requete) {
        Commande commande = findById(id);
        if (commande.getStatut() != StatutCommande.EN_ATTENTE || commande.isPayee()) {
            throw new RegleMetierException("Seule une commande en attente et non payée peut être modifiée");
        }
        commande.getLignes().clear();
        appliquer(commande, requete);
        return commande;
    }

    @Override
    public Commande changerStatut(Integer id, StatutCommande nouveau) {
        Commande commande = findById(id);
        StatutCommande actuel = commande.getStatut();
        if (actuel == nouveau) {
            return commande;
        }
        if (actuel == StatutCommande.ANNULEE) {
            throw new RegleMetierException("Une commande annulée ne peut plus changer de statut");
        }
        if (nouveau == StatutCommande.ANNULEE && commande.isPayee()) {
            throw new RegleMetierException("Une commande déjà payée ne peut pas être annulée");
        }
        boolean livraison = commande.getType() == TypeCommande.LIVRAISON;
        if (nouveau == StatutCommande.LIVREE && !livraison) {
            throw new RegleMetierException("Seules les commandes en livraison peuvent être marquées livrées");
        }
        if (nouveau == StatutCommande.SERVIE && livraison) {
            throw new RegleMetierException("Une commande en livraison se termine par « Livrée »");
        }
        commande.setStatut(nouveau);

        Livraison liv = commande.getLivraison();
        if (liv != null) {
            if (nouveau == StatutCommande.LIVREE) {
                liv.setStatut(StatutLivraison.LIVREE);
                liv.setDateLivraison(LocalDate.now());
            } else if (nouveau == StatutCommande.ANNULEE) {
                liv.setStatut(StatutLivraison.ECHOUEE);
            }
        }
        return commande;
    }

    @Override
    public Commande payer(Integer id, String methode) {
        Commande commande = findById(id);
        if (commande.isPayee()) {
            throw new RegleMetierException("Cette commande est déjà payée");
        }
        if (commande.getStatut() == StatutCommande.ANNULEE) {
            throw new RegleMetierException("Une commande annulée ne peut pas être payée");
        }
        String m = methode == null ? "" : methode.trim().toUpperCase();
        if (!Paiement.METHODES.contains(m)) {
            throw new RegleMetierException("Moyen de paiement inconnu : " + methode);
        }
        Paiement paiement = new Paiement();
        paiement.setCommande(commande);
        paiement.setMethode(m);
        paiement.setMontant(commande.getMontantTotal() == null ? 0 : commande.getMontantTotal());
        paiement.setDatePaiement(LocalDate.now());
        commande.setPaiement(paiement);
        return commande;
    }

    @Override
    public void delete(Integer id) {
        commandeRepository.delete(findById(id));
    }

    /** Recopie la requête dans la commande et recalcule le montant à partir des prix en base. */
    private void appliquer(Commande commande, CommandeRequest requete) {
        TypeCommande type = requete.type() == null ? TypeCommande.SUR_PLACE : requete.type();
        commande.setType(type);
        commande.setNotes(requete.notes());
        commande.setNumeroTable(type == TypeCommande.SUR_PLACE ? requete.numeroTable() : null);

        Client client = null;
        if (requete.clientId() != null) {
            client = clientRepository.findById(requete.clientId())
                    .orElseThrow(() -> new RessourceIntrouvableException("Client", requete.clientId()));
        }
        commande.setClient(client);

        for (LigneRequest l : requete.lignes()) {
            Plat plat = platRepository.findById(l.platId())
                    .orElseThrow(() -> new RessourceIntrouvableException("Plat", l.platId()));
            if (!plat.isCommandable()) {
                throw new RegleMetierException("Le plat « " + plat.getNom() + " » n'est pas disponible");
            }
            commande.ajouterLigne(new LigneCommande(plat, l.quantite()));
        }
        commande.recalculerTotal();

        if (type == TypeCommande.LIVRAISON) {
            String adresse = requete.adresseLivraison() != null && !requete.adresseLivraison().isBlank()
                    ? requete.adresseLivraison().trim()
                    : client != null ? client.getAddress() : null;
            if (adresse == null || adresse.isBlank()) {
                throw new RegleMetierException("Une adresse de livraison est obligatoire");
            }
            Livraison livraison = commande.getLivraison();
            if (livraison == null) {
                livraison = new Livraison();
                livraison.setStatut(StatutLivraison.A_ASSIGNER);
                livraison.setCommande(commande);
                commande.setLivraison(livraison);
            }
            livraison.setAdresseDestination(adresse);
        } else if (commande.getLivraison() != null) {
            Livraison obsolete = commande.getLivraison();
            commande.setLivraison(null);
            livraisonRepository.delete(obsolete);
        }
    }
}

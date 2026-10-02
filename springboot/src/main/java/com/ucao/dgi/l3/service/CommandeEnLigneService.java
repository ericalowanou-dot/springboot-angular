package com.ucao.dgi.l3.service;

import com.ucao.dgi.l3.dto.CommandeDtos.CommandeRequest;
import com.ucao.dgi.l3.dto.EnLigneDtos.CommandeEnLigneRequest;
import com.ucao.dgi.l3.dto.EnLigneDtos.CommandeEnLigneResponse;
import com.ucao.dgi.l3.dto.EnLigneDtos.SuiviCommande;
import com.ucao.dgi.l3.entity.Client;
import com.ucao.dgi.l3.entity.Commande;
import com.ucao.dgi.l3.entity.TypeCommande;
import com.ucao.dgi.l3.exception.RegleMetierException;
import com.ucao.dgi.l3.exception.RessourceIntrouvableException;
import com.ucao.dgi.l3.repository.ClientRepository;
import com.ucao.dgi.l3.repository.CommandeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Optional;

/** Commandes passées par les clients depuis la carte publique, et leur suivi par code. */
@Service
@Transactional
@RequiredArgsConstructor
public class CommandeEnLigneService {

    // sans 0/O ni 1/I pour éviter les confusions à la lecture
    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int QUANTITE_MAX = 50;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final CommandeService commandeService;
    private final CommandeRepository commandeRepository;
    private final ClientRepository clientRepository;

    public CommandeEnLigneResponse commander(CommandeEnLigneRequest r) {
        if (r.type() == TypeCommande.SUR_PLACE) {
            throw new RegleMetierException("En ligne, choisissez « À emporter » ou « Livraison »");
        }
        if (r.lignes().stream().anyMatch(l -> l.quantite() > QUANTITE_MAX)) {
            throw new RegleMetierException("Quantité trop importante : contactez directement le restaurant");
        }
        String telephone = r.telephone().trim();
        Client client = clientParTelephone(telephone).orElse(null);
        String adresse = r.adresse() == null ? null : r.adresse().trim();

        // même logique (prix, disponibilité, livraison) que pour une commande saisie en caisse
        Commande commande = commandeService.creer(new CommandeRequest(
                client == null ? null : client.getIdClient(), r.type(), null, r.notes(), adresse, r.lignes()));
        commande.setEnLigne(true);
        commande.setNomContact(r.nom().trim());
        commande.setTelephoneContact(telephone);
        commande.setCodeSuivi(nouveauCode());
        return new CommandeEnLigneResponse(commande.getCodeSuivi(), commande.getIdCommande(),
                commande.getMontantTotal() == null ? 0 : commande.getMontantTotal());
    }

    @Transactional(readOnly = true)
    public SuiviCommande suivre(String code) {
        String c = code == null ? "" : code.trim().toUpperCase();
        return commandeRepository.findByCodeSuivi(c)
                .map(SuiviCommande::of)
                .orElseThrow(() -> new RessourceIntrouvableException("Commande", c));
    }

    /** Rattache la commande à la fiche client existante qui a le même numéro de téléphone. */
    private Optional<Client> clientParTelephone(String telephone) {
        String cherche = chiffres(telephone);
        if (cherche.length() < 8) {
            return Optional.empty();
        }
        String fin = cherche.substring(cherche.length() - 8);
        return clientRepository.findAll().stream()
                .filter(c -> c.getTelephone() != null && chiffres(c.getTelephone()).endsWith(fin))
                .findFirst();
    }

    private String nouveauCode() {
        String code;
        do {
            StringBuilder sb = new StringBuilder(8);
            for (int i = 0; i < 8; i++) {
                sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
            }
            code = sb.toString();
        } while (commandeRepository.existsByCodeSuivi(code));
        return code;
    }

    private static String chiffres(String s) {
        return s.replaceAll("\\D", "");
    }
}

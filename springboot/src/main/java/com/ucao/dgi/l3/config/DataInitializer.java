package com.ucao.dgi.l3.config;

import com.ucao.dgi.l3.entity.*;
import com.ucao.dgi.l3.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Au démarrage :
 * 1. crée le compte administrateur s'il n'existe aucun utilisateur ;
 * 2. si app.demo-data=true et que la base est vide, insère un jeu de données de démonstration.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final CategoriePlatRepository categorieRepository;
    private final PlatRepository platRepository;
    private final MenuRepository menuRepository;
    private final ClientRepository clientRepository;
    private final PersonnelRepository personnelRepository;
    private final FournisseurRepository fournisseurRepository;
    private final ProduitRepository produitRepository;
    private final CommandeRepository commandeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;
    private final PlatformTransactionManager transactionManager;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.demo-data:false}")
    private boolean demoData;

    @Override
    public void run(String... args) {
        mettreAJourContraintes();
        new TransactionTemplate(transactionManager).executeWithoutResult(statut -> initialiser());
    }

    /**
     * Une base créée par une version précédente garde des contraintes CHECK sur les colonnes d'énumération
     * (rôles, statuts…) qui ignorent les nouvelles valeurs : Hibernate ne les met jamais à jour. On les
     * recrée avec les valeurs actuelles. Exécuté hors transaction : un échec n'empêche pas le démarrage.
     */
    private void mettreAJourContraintes() {
        contrainte("users", "role", User.Role.values());
        contrainte("livraison", "statut", StatutLivraison.values());
        contrainte("rad_commande", "statut", StatutCommande.values());
        contrainte("rad_commande", "type", TypeCommande.values());
    }

    private void contrainte(String table, String colonne, Enum<?>[] valeurs) {
        String nom = table + "_" + colonne + "_check";
        String liste = Arrays.stream(valeurs).map(v -> "'" + v.name() + "'").collect(Collectors.joining(","));
        try {
            // H2 (développement) stocke ces colonnes dans un type ENUM figé : on repasse en texte
            if (estH2()) {
                jdbcTemplate.execute("ALTER TABLE " + table + " ALTER COLUMN " + colonne + " SET DATA TYPE VARCHAR(20)");
            }
            jdbcTemplate.execute("ALTER TABLE " + table + " DROP CONSTRAINT IF EXISTS " + nom);
            jdbcTemplate.execute("ALTER TABLE " + table + " ADD CONSTRAINT " + nom
                    + " CHECK (" + colonne + " IN (" + liste + "))");
        } catch (DataAccessException e) {
            log.warn("Contrainte {} non mise à jour : {}", nom, e.getMostSpecificCause().getMessage());
        }
    }

    private boolean estH2() {
        String produit = jdbcTemplate.execute((ConnectionCallback<String>) c -> c.getMetaData().getDatabaseProductName());
        return produit != null && produit.toUpperCase().contains("H2");
    }

    private void initialiser() {
        boolean baseNeuve = userRepository.count() == 0;
        if (baseNeuve) {
            creerUtilisateur(adminEmail, adminPassword, "Principal", "Admin", User.Role.ADMIN);
            log.info("Compte administrateur créé : {}", adminEmail);
            if (demoData) {
                creerUtilisateur("gerant@restaurant.com", "gerant123", "Mensah", "Akossiwa", User.Role.GERANT);
                creerUtilisateur("employe@restaurant.com", "employe123", "Agbeko", "Kossi", User.Role.EMPLOYE);
            }
        }
        if (demoData && categorieRepository.count() == 0 && platRepository.count() == 0) {
            insererDemo();
            log.info("Données de démonstration insérées");
        }
        // compte livreur de démonstration, uniquement lors de la toute première initialisation
        if (baseNeuve && demoData) {
            personnelRepository.findAllByFonctionIgnoreCase("LIVREUR").stream().findFirst().ifPresent(livreur ->
                    userRepository.save(User.builder()
                            .email("livreur@restaurant.com")
                            .password(passwordEncoder.encode("livreur123"))
                            .nom(livreur.getNom())
                            .prenom(livreur.getPrenom())
                            .telephone(livreur.getTelephone())
                            .role(User.Role.LIVREUR)
                            .personnel(livreur)
                            .build()));
        }
    }

    private void creerUtilisateur(String email, String motDePasse, String nom, String prenom, User.Role role) {
        userRepository.save(User.builder()
                .email(email.trim().toLowerCase())
                .password(passwordEncoder.encode(motDePasse))
                .nom(nom)
                .prenom(prenom)
                .role(role)
                .build());
    }

    private void insererDemo() {
        CategoriePlat entrees = categorieRepository.save(new CategoriePlat("Entrées", "Pour ouvrir l'appétit"));
        CategoriePlat plats = categorieRepository.save(new CategoriePlat("Plats", "Spécialités de la maison"));
        CategoriePlat grillades = categorieRepository.save(new CategoriePlat("Grillades", "Cuites au feu de bois"));
        CategoriePlat desserts = categorieRepository.save(new CategoriePlat("Desserts", "Douceurs maison"));
        CategoriePlat boissons = categorieRepository.save(new CategoriePlat("Boissons", "Fraîches et naturelles"));

        List<Plat> carte = platRepository.saveAll(List.of(
                new Plat("Salade d'avocat", 1500.0, "Avocat, tomates, oignons et vinaigrette citronnée", entrees),
                new Plat("Beignets de haricots", 1000.0, "Accras croustillants, sauce pimentée", entrees),
                new Plat("Fufu sauce arachide", 3500.0, "Fufu d'igname pilée, sauce arachide au poulet", plats),
                new Plat("Riz au gras", 3000.0, "Riz cuisiné à la tomate, légumes et bœuf", plats),
                new Plat("Ayimolou", 2000.0, "Riz et haricots, sauce tomate pimentée et œuf", plats),
                new Plat("Poulet bicyclette braisé", 5000.0, "Poulet fermier mariné, alloco et piment", grillades),
                new Plat("Poisson braisé", 6000.0, "Tilapia braisé, attiéké et sauce crudités", grillades),
                new Plat("Brochettes de bœuf", 2500.0, "Trois brochettes épicées au kankankan", grillades),
                new Plat("Salade de fruits", 1200.0, "Mangue, ananas, papaye et menthe", desserts),
                new Plat("Dégué", 1000.0, "Mil au lait caillé sucré", desserts),
                new Plat("Bissap", 700.0, "Infusion d'hibiscus glacée", boissons),
                new Plat("Jus de gingembre", 700.0, "Gingembre frais pressé et citron", boissons)));

        Menu midi = new Menu();
        midi.setNom("Formule midi");
        midi.setDescription("Entrée + plat + boisson");
        midi.setPrix(4500);
        midi.setPlats(new java.util.ArrayList<>(List.of(carte.get(0), carte.get(3), carte.get(10))));
        Menu prestige = new Menu();
        prestige.setNom("Menu prestige");
        prestige.setDescription("Le meilleur de nos grillades avec dessert");
        prestige.setPrix(8500);
        prestige.setPlats(new java.util.ArrayList<>(List.of(carte.get(1), carte.get(6), carte.get(8), carte.get(11))));
        menuRepository.saveAll(List.of(midi, prestige));

        List<Client> clients = clientRepository.saveAll(List.of(
                new Client("Amegah", "Komla", "Bè, Lomé", "+228 90 11 22 33", "komla@mail.tg"),
                new Client("Kouassi", "Ama", "Tokoin, Lomé", "+228 91 22 33 44", "ama.k@mail.tg"),
                new Client("Diallo", "Moussa", "Agoè, Lomé", "+228 92 33 44 55", "moussa@mail.tg"),
                new Client("Lawson", "Edem", "Adidogomé, Lomé", "+228 93 44 55 66", "edem@mail.tg"),
                new Client("Tchalla", "Yawa", "Nyékonakpoè", "+228 70 55 66 77", "yawa.t@mail.tg")));

        Personnel livreur1 = new Personnel("Adjo", "Kodjo", "LIVREUR", "+228 90 00 00 01", 80000.0);
        Personnel livreur2 = new Personnel("Folly", "Sena", "LIVREUR", "+228 90 00 00 02", 80000.0);
        personnelRepository.saveAll(List.of(
                new Personnel("Gbadamassi", "Rachid", "CHEF", "+228 90 00 00 03", 250000.0),
                new Personnel("Ayivi", "Mawuli", "CUISINIER", "+228 90 00 00 04", 120000.0),
                new Personnel("Dossou", "Afi", "SERVEUR", "+228 90 00 00 05", 90000.0),
                new Personnel("Kpodar", "Elom", "CAISSIER", "+228 90 00 00 06", 100000.0),
                livreur1, livreur2));

        Fournisseur marche = fournisseurRepository.save(new Fournisseur("Grand Marché de Lomé", "Mme Abla",
                "+228 22 21 00 00", "contact@grandmarche.tg", "Rue du Commerce, Lomé"));
        Fournisseur ferme = fournisseurRepository.save(new Fournisseur("Ferme Avicole du Golfe", "M. Koffi",
                "+228 22 25 11 11", "ventes@fermegolfe.tg", "Route d'Aného"));

        produitRepository.saveAll(List.of(
                produit("Riz parfumé", 650.0, 20, "kg", marche, 45),
                produit("Huile d'arachide", 1500.0, 10, "L", marche, 8),
                produit("Tomates", 800.0, 15, "kg", marche, 30),
                produit("Poulet fermier", 3500.0, 10, "pièce", ferme, 6),
                produit("Œufs", 100.0, 60, "pièce", ferme, 120),
                produit("Igname", 500.0, 25, "kg", marche, 40)));

        // Historique de commandes sur 7 jours pour animer le tableau de bord
        Random random = new Random(42);
        StatutCommande[] enCours = {StatutCommande.EN_ATTENTE, StatutCommande.EN_PREPARATION, StatutCommande.PRETE};
        String[] methodes = {"ESPECES", "MOBILE_MONEY", "CARTE"};
        for (int jour = 6; jour >= 0; jour--) {
            int nb = 3 + random.nextInt(4);
            for (int i = 0; i < nb; i++) {
                Commande c = new Commande();
                LocalDate date = LocalDate.now().minusDays(jour);
                c.setDateCommande(date);
                c.setCreeLe(LocalDateTime.of(date, java.time.LocalTime.of(11 + random.nextInt(10), random.nextInt(60))));
                boolean livraison = random.nextInt(4) == 0;
                c.setType(livraison ? TypeCommande.LIVRAISON
                        : random.nextBoolean() ? TypeCommande.SUR_PLACE : TypeCommande.A_EMPORTER);
                if (c.getType() == TypeCommande.SUR_PLACE) {
                    c.setNumeroTable(1 + random.nextInt(12));
                }
                Client client = random.nextInt(3) == 0 ? null : clients.get(random.nextInt(clients.size()));
                if (livraison && client == null) {
                    client = clients.get(0);
                }
                c.setClient(client);
                int nbLignes = 1 + random.nextInt(3);
                for (int l = 0; l < nbLignes; l++) {
                    c.ajouterLigne(new LigneCommande(carte.get(random.nextInt(carte.size())), 1 + random.nextInt(3)));
                }
                c.recalculerTotal();

                boolean aujourdhui = jour == 0;
                if (aujourdhui && i < 3) {
                    c.setStatut(enCours[i]);
                } else {
                    c.setStatut(livraison ? StatutCommande.LIVREE : StatutCommande.SERVIE);
                    Paiement p = new Paiement();
                    p.setCommande(c);
                    p.setDatePaiement(date);
                    p.setMontant(c.getMontantTotal());
                    p.setMethode(methodes[random.nextInt(methodes.length)]);
                    c.setPaiement(p);
                }
                if (livraison) {
                    Livraison liv = new Livraison();
                    liv.setCommande(c);
                    liv.setAdresseDestination(client.getAddress());
                    if (c.getStatut() == StatutCommande.LIVREE) {
                        liv.setStatut(StatutLivraison.LIVREE);
                        liv.setLivreur(random.nextBoolean() ? livreur1 : livreur2);
                        liv.setDateLivraison(date);
                        liv.setHeureFin(c.getCreeLe().plusMinutes(40));
                    } else if (i % 2 == 0) {
                        liv.setStatut(StatutLivraison.ASSIGNEE);
                        liv.setLivreur(livreur1);
                    } else {
                        liv.setStatut(StatutLivraison.A_ASSIGNER);
                    }
                    c.setLivraison(liv);
                }
                commandeRepository.save(c);
            }
        }
    }

    private Produit produit(String nom, Double prix, int seuil, String unite, Fournisseur f, int quantite) {
        Produit p = new Produit();
        p.setNom(nom);
        p.setPrixUnitaire(prix);
        p.setSeuil(seuil);
        p.setUnite(unite);
        p.setFournisseur(f);
        p.setStock(new Stock(p, quantite));
        return p;
    }
}

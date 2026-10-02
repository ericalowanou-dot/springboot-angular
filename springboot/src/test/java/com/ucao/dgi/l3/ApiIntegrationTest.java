package com.ucao.dgi.l3;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ApiIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper json;

    private String token;

    @BeforeEach
    void connexion() throws Exception {
        String reponse = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"admin@test.com\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        token = json.readTree(reponse).get("token").asText();
    }


    @Test
    void refuseLesIdentifiantsInvalides() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"admin@test.com\",\"password\":\"mauvais\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Email ou mot de passe incorrect"));
    }

    @Test
    void protegeLesRoutesPriveesMaisPasLaCarte() throws Exception {
        mvc.perform(get("/api/commandes")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/plats")).andExpect(status().isOk());
        mvc.perform(post("/api/plats").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
        // les anciens endpoints sans /api ne sont plus exposés
        mvc.perform(get("/clients/find_all").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void calculeLeTotalCoteServeurEtGereLePaiement() throws Exception {
        int platA = creerPlat("Riz au gras", 3000);
        int platB = creerPlat("Bissap", 500);

        // le client HTTP ne peut pas imposer le montant : il est recalculé
        JsonNode commande = appel(post("/api/commandes"), """
                {"type":"SUR_PLACE","numeroTable":4,"montantTotal":1,
                 "lignes":[{"platId":%d,"quantite":2},{"platId":%d,"quantite":3}]}
                """.formatted(platA, platB), 201);
        int id = commande.get("idCommande").asInt();
        assertThat(commande.get("montantTotal").asDouble()).isEqualTo(7500.0);
        assertThat(commande.get("statut").asText()).isEqualTo("EN_ATTENTE");

        JsonNode payee = appel(post("/api/commandes/" + id + "/paiement"), "{\"methode\":\"especes\"}", 200);
        assertThat(payee.get("payee").asBoolean()).isTrue();
        assertThat(payee.get("paiement").get("montant").asDouble()).isEqualTo(7500.0);

        // double paiement et annulation après paiement interdits
        appel(post("/api/commandes/" + id + "/paiement"), "{\"methode\":\"CARTE\"}", 409);
        appel(patch("/api/commandes/" + id + "/statut"), "{\"statut\":\"ANNULEE\"}", 409);
    }

    @Test
    void creeUneLivraisonEtValideLesDonnees() throws Exception {
        int plat = creerPlat("Poisson braisé", 6000);
        appel(post("/api/commandes"), "{\"type\":\"LIVRAISON\",\"lignes\":[{\"platId\":%d,\"quantite\":1}]}"
                .formatted(plat), 409); // adresse manquante
        JsonNode cmd = appel(post("/api/commandes"),
                "{\"type\":\"LIVRAISON\",\"adresseLivraison\":\"Bè, Lomé\",\"lignes\":[{\"platId\":%d,\"quantite\":1}]}"
                        .formatted(plat), 201);
        assertThat(cmd.get("livraison").get("statut").asText()).isEqualTo("A_ASSIGNER");

        appel(post("/api/commandes"), "{\"lignes\":[]}", 400);
        appel(post("/api/plats"), "{\"nom\":\"\",\"prix\":-1}", 400);
    }

    @Test
    void approvisionnementAlimenteLeStock() throws Exception {
        int fournisseur = appel(post("/api/fournisseurs"), "{\"nom\":\"Marché\"}", 201).get("idFournisseur").asInt();
        int produit = appel(post("/api/produits"), """
                {"nom":"Riz","prixUnitaire":600,"seuil":10,"unite":"kg","fournisseurId":%d,"quantite":5}
                """.formatted(fournisseur), 201).get("idProduit").asInt();

        int appro = appel(post("/api/approvisionnements"), """
                {"fournisseurId":%d,"lignes":[{"produitId":%d,"quantite":20,"prixUnitaire":550}]}
                """.formatted(fournisseur, produit), 201).get("idCmdInt").asInt();
        appel(post("/api/approvisionnements/" + appro + "/reception"), "", 200);

        JsonNode p = appel(get("/api/produits/" + produit), null, 200);
        assertThat(p.get("quantiteStock").asInt()).isEqualTo(25);
        assertThat(p.get("enAlerte").asBoolean()).isFalse();
    }

    @Test
    void parcoursCompletDuLivreur() throws Exception {
        int plat = creerPlat("Poulet braisé", 5000);
        int livreurId = appel(post("/api/personnel"),
                "{\"nom\":\"Adjo\",\"prenom\":\"Kodjo\",\"fonction\":\"LIVREUR\"}", 201).get("idPersonnel").asInt();
        int serveurId = appel(post("/api/personnel"),
                "{\"nom\":\"Dossou\",\"prenom\":\"Afi\",\"fonction\":\"SERVEUR\"}", 201).get("idPersonnel").asInt();

        // seul un employé de fonction LIVREUR peut recevoir un accès livreur
        appel(post("/api/personnel/" + serveurId + "/acces"), "{\"email\":\"afi@test.com\",\"password\":\"secret1\"}", 409);
        appel(post("/api/personnel/" + livreurId + "/acces"), "{\"email\":\"kodjo@test.com\",\"password\":\"secret1\"}", 201);
        appel(post("/api/personnel/" + livreurId + "/acces"), "{\"email\":\"autre@test.com\",\"password\":\"secret1\"}", 409);
        // un compte livreur ne se crée pas depuis la gestion des utilisateurs
        appel(post("/api/users"), "{\"email\":\"x@test.com\",\"nom\":\"X\",\"prenom\":\"Y\",\"role\":\"LIVREUR\",\"password\":\"secret1\"}", 409);

        JsonNode cmd = appel(post("/api/commandes"),
                "{\"type\":\"LIVRAISON\",\"adresseLivraison\":\"Bè\",\"lignes\":[{\"platId\":%d,\"quantite\":1}]}".formatted(plat), 201);
        int livraison = cmd.get("livraison").get("idLivraison").asInt();
        JsonNode assignee = appel(patch("/api/livraisons/" + livraison), "{\"livreurId\":%d}".formatted(livreurId), 200);
        assertThat(assignee.get("statut").asText()).isEqualTo("ASSIGNEE");

        String tokenAdmin = token;
        token = jeton("kodjo@test.com", "secret1");

        // le livreur ne voit que son espace
        appel(get("/api/commandes"), null, 403);
        appel(get("/api/personnel"), null, 403);
        assertThat(appel(get("/api/livreur/livraisons"), null, 200)).hasSize(1);

        appel(post("/api/livreur/livraisons/" + livraison + "/livree"), "", 409); // doit d'abord partir
        JsonNode parti = appel(post("/api/livreur/livraisons/" + livraison + "/depart"), "", 200);
        assertThat(parti.get("statut").asText()).isEqualTo("EN_COURS");
        assertThat(parti.get("heureDepart").isNull()).isFalse();

        appel(post("/api/livreur/livraisons/" + livraison + "/echec"), "{\"motif\":\"\"}", 400);
        JsonNode echec = appel(post("/api/livreur/livraisons/" + livraison + "/echec"),
                "{\"motif\":\"Client absent\",\"commentaire\":\"pas de réponse au téléphone\"}", 200);
        assertThat(echec.get("statut").asText()).isEqualTo("ECHOUEE");
        assertThat(echec.get("motifEchec").asText()).isEqualTo("Client absent — pas de réponse au téléphone");

        // l'équipe réassigne : le livreur repart
        token = tokenAdmin;
        assertThat(appel(patch("/api/livraisons/" + livraison), "{\"livreurId\":%d}".formatted(livreurId), 200)
                .get("statut").asText()).isEqualTo("ASSIGNEE");
        token = jeton("kodjo@test.com", "secret1");
        appel(post("/api/livreur/livraisons/" + livraison + "/depart"), "", 200);
        appel(post("/api/livreur/livraisons/" + livraison + "/livree"), "", 200);

        token = tokenAdmin;
        assertThat(appel(get("/api/commandes/" + cmd.get("idCommande").asInt()), null, 200)
                .get("statut").asText()).isEqualTo("LIVREE");
        assertThat(appel(get("/api/dashboard"), null, 200).get("livraisonsLivreesJour").asLong()).isEqualTo(1);

        // la suppression de la fiche supprime aussi l'accès
        int autre = appel(post("/api/personnel"),
                "{\"nom\":\"Folly\",\"prenom\":\"Sena\",\"fonction\":\"LIVREUR\"}", 201).get("idPersonnel").asInt();
        appel(post("/api/personnel/" + autre + "/acces"), "{\"email\":\"sena@test.com\",\"password\":\"secret1\"}", 201);
        appel(delete("/api/personnel/" + autre), null, 204);
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"sena@test.com\",\"password\":\"secret1\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void commandeEnLigneSansCompteEtSuiviParCode() throws Exception {
        int plat = creerPlat("Riz au gras", 3000);
        String tokenAdmin = token;
        token = null; // le client n'est pas connecté

        appel(post("/api/public/commandes"), """
                {"type":"SUR_PLACE","nom":"Ama","telephone":"90112233","lignes":[{"platId":%d,"quantite":1}]}
                """.formatted(plat), 409);
        appel(post("/api/public/commandes"), """
                {"type":"LIVRAISON","nom":"Ama","telephone":"abc","lignes":[{"platId":%d,"quantite":1}]}
                """.formatted(plat), 400);

        JsonNode recu = appel(post("/api/public/commandes"), """
                {"type":"LIVRAISON","nom":"Ama Kouassi","telephone":"+228 90 11 22 33","adresse":"Tokoin",
                 "montantTotal":1,"lignes":[{"platId":%d,"quantite":2}]}
                """.formatted(plat), 201);
        String code = recu.get("codeSuivi").asText();
        assertThat(code).hasSize(8);
        assertThat(recu.get("montantTotal").asDouble()).isEqualTo(6000.0);

        // suivi public : statut et contenu, sans données personnelles
        JsonNode suivi = appel(get("/api/public/commandes/" + code.toLowerCase()), null, 200);
        assertThat(suivi.get("statut").asText()).isEqualTo("EN_ATTENTE");
        assertThat(suivi.get("statutLivraison").asText()).isEqualTo("A_ASSIGNER");
        assertThat(suivi.toString()).doesNotContain("90 11 22 33").doesNotContain("Tokoin");
        appel(get("/api/public/commandes/INCONNU1"), null, 404);
        // le reste de l'API reste fermé
        appel(get("/api/commandes"), null, 401);

        // l'équipe voit la commande en ligne avec les coordonnées du client
        token = tokenAdmin;
        JsonNode cote = appel(get("/api/commandes/" + recu.get("numero").asInt()), null, 200);
        assertThat(cote.get("enLigne").asBoolean()).isTrue();
        assertThat(cote.get("nomContact").asText()).isEqualTo("Ama Kouassi");
        assertThat(cote.get("livraison").get("adresseDestination").asText()).isEqualTo("Tokoin");

        // anti-abus : 5 commandes par IP et par fenêtre de 10 minutes (2 déjà comptées ci-dessus)
        token = null;
        String corps = "{\"type\":\"A_EMPORTER\",\"nom\":\"Robot\",\"telephone\":\"90000000\",\"lignes\":[{\"platId\":%d,\"quantite\":1}]}"
                .formatted(plat);
        for (int i = 0; i < 3; i++) {
            appel(post("/api/public/commandes"), corps, 201);
        }
        appel(post("/api/public/commandes"), corps, 429);
    }

    private String jeton(String email, String motDePasse) throws Exception {
        String reponse = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"%s\",\"password\":\"%s\"}".formatted(email, motDePasse)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return json.readTree(reponse).get("token").asText();
    }

    private int creerPlat(String nom, double prix) throws Exception {
        return appel(post("/api/plats"), "{\"nom\":\"%s\",\"prix\":%s}".formatted(nom, prix), 201)
                .get("idPlat").asInt();
    }

    private JsonNode appel(MockHttpServletRequestBuilder requete, String corps, int statutAttendu) throws Exception {
        requete.header("Authorization", "Bearer " + token);
        if (corps != null) {
            requete.contentType(MediaType.APPLICATION_JSON).content(corps);
        }
        String reponse = mvc.perform(requete).andExpect(status().is(statutAttendu))
                .andReturn().getResponse().getContentAsString();
        return reponse.isEmpty() ? null : json.readTree(reponse);
    }
}

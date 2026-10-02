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

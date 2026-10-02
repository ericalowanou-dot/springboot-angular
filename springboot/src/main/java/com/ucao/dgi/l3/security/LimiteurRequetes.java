package com.ucao.dgi.l3.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Limite le nombre de commandes en ligne par adresse IP sur une fenêtre glissante,
 * pour éviter qu'un robot ne remplisse la cuisine de fausses commandes.
 */
@Component
public class LimiteurRequetes {

    private static final int MAX = 5;
    private static final Duration FENETRE = Duration.ofMinutes(10);

    private final Map<String, Deque<Instant>> historique = new ConcurrentHashMap<>();

    /** true si la requête est acceptée (et l'enregistre), false si la limite est atteinte. */
    public boolean autoriser(HttpServletRequest requete) {
        String ip = ip(requete);
        Instant maintenant = Instant.now();
        Deque<Instant> instants = historique.computeIfAbsent(ip, k -> new ArrayDeque<>());
        synchronized (instants) {
            while (!instants.isEmpty() && instants.peekFirst().isBefore(maintenant.minus(FENETRE))) {
                instants.pollFirst();
            }
            if (instants.size() >= MAX) {
                return false;
            }
            instants.addLast(maintenant);
        }
        if (historique.size() > 10_000) {
            historique.entrySet().removeIf(e -> e.getValue().isEmpty());
        }
        return true;
    }

    /** Derrière le proxy de Render, l'IP réelle est la première de X-Forwarded-For. */
    private static String ip(HttpServletRequest requete) {
        String transmise = requete.getHeader("X-Forwarded-For");
        if (transmise != null && !transmise.isBlank()) {
            return transmise.split(",")[0].trim();
        }
        return requete.getRemoteAddr();
    }
}

package com.ucao.dgi.l3.entity;

/** Cycle de vie d'une commande client. */
public enum StatutCommande {
    EN_ATTENTE,
    EN_PREPARATION,
    PRETE,
    SERVIE,
    LIVREE,
    ANNULEE;

    public boolean estTerminee() {
        return this == SERVIE || this == LIVREE || this == ANNULEE;
    }
}

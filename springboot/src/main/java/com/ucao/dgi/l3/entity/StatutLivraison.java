package com.ucao.dgi.l3.entity;

/** A_ASSIGNER → ASSIGNEE (livreur choisi) → EN_COURS (« Je pars ») → LIVREE ou ECHOUEE. */
public enum StatutLivraison {
    A_ASSIGNER,
    ASSIGNEE,
    EN_COURS,
    LIVREE,
    ECHOUEE;

    public boolean estTerminee() {
        return this == LIVREE || this == ECHOUEE;
    }
}

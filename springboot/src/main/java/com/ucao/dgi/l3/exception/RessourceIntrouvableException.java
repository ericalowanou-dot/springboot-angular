package com.ucao.dgi.l3.exception;

/** Levée quand une entité demandée n'existe pas : traduite en HTTP 404. */
public class RessourceIntrouvableException extends RuntimeException {

    public RessourceIntrouvableException(String ressource, Object id) {
        super(ressource + " introuvable (id = " + id + ")");
    }
}

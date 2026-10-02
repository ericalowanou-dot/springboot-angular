package com.ucao.dgi.l3.exception;

/** Levée quand une opération viole une règle de gestion : traduite en HTTP 409. */
public class RegleMetierException extends RuntimeException {

    public RegleMetierException(String message) {
        super(message);
    }
}

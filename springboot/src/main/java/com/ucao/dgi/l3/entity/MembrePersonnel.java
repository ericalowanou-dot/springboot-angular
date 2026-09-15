package com.ucao.dgi.l3.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Classe concrète pour représenter un membre du personnel générique
 * Utilisée pour la désérialisation JSON lorsque le type exact n'est pas spécifique
 */
@Entity
@Table(name = "membre_personnel")
public class MembrePersonnel extends Personnel {
    // Cette classe hérite de tous les champs de Personnel
    // Elle permet de créer des instances concrètes pour la désérialisation
}


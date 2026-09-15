package com.ucao.dgi.l3.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity(name = "serveur")
@Table
public class Serveur implements Serializable {
    @Id
    private String id;
    @Column(length = 50, nullable = false)
    private String nom;
    @Column(length = 50, nullable = false)
    private String prenom;
}

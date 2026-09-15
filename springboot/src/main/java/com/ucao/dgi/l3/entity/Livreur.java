package com.ucao.dgi.l3.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity(name = "livreur")
@Table
public class Livreur implements Serializable {
    @Id
    private String id;
    @Column(length = 50, nullable = false)
    private String nom;
    @Column(length = 50, nullable = false)
    private String prenom;

}


package com.ucao.dgi.l3.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity(name = "chef")
@Table
public class Chef implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 50, nullable = false)
    private String nom;
    @Column(length = 50, nullable = false)
    private String prenom;
}

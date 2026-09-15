package com.ucao.dgi.l3.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "personnel")
@Inheritance(strategy = InheritanceType.JOINED) // chaque sous-classe aura sa table
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Personnel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPersonnel;

    private String nom;
    private String prenom;
    private String fonction;
}

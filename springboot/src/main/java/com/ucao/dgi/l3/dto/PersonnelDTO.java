package com.ucao.dgi.l3.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour Personnel
 * Utilisé pour recevoir les données depuis le frontend
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonnelDTO {
    private Integer idPersonnel;
    private String nom;
    private String prenom;
    private String fonction;
    private String email;
    private String telephone;
}


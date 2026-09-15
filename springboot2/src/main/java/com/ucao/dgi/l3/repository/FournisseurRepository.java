package com.ucao.dgi.l3.repository;

import com.ucao.dgi.l3.entity.Fournisseur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FournisseurRepository extends JpaRepository<Fournisseur, Integer> {
    Fournisseur findByNom(String nom);
}

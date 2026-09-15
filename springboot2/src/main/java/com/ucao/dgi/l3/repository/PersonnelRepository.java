package com.ucao.dgi.l3.repository;

import com.ucao.dgi.l3.entity.Personnel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonnelRepository extends JpaRepository<Personnel, Integer> {

    // Trouver un personnel selon sa fonction (Serveur, Livreur, Chef)
    Personnel findByFonction(String fonction);
}

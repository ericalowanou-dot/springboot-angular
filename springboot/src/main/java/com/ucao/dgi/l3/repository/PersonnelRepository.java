package com.ucao.dgi.l3.repository;

import com.ucao.dgi.l3.entity.Personnel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonnelRepository extends JpaRepository<Personnel, Integer> {

    // Trouver le personnel selon sa fonction (SERVEUR, LIVREUR, CHEF...)
    List<Personnel> findAllByFonctionIgnoreCase(String fonction);
}

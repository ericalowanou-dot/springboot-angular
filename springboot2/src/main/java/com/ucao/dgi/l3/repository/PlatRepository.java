package com.ucao.dgi.l3.repository;

import com.ucao.dgi.l3.entity.Plat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlatRepository extends JpaRepository<Plat, Integer> {

    List<Plat> findByCategorieIdCategorie(Integer idCategorie);

    Plat findByNom(String nom);

    Plat findPlatByPrixBetween(Double min, Double max);
}

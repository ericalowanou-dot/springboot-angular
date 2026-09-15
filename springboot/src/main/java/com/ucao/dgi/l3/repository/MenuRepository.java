package com.ucao.dgi.l3.repository;

import com.ucao.dgi.l3.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Integer> {

    Menu findByNom(String nom);
}

package com.ucao.dgi.l3.repository;

import com.ucao.dgi.l3.entity.Livreur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LivreurRepository extends JpaRepository<Livreur, String> {
}

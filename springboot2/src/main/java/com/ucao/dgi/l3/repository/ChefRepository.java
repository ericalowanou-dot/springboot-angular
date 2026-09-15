package com.ucao.dgi.l3.repository;

import com.ucao.dgi.l3.entity.Chef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChefRepository extends JpaRepository<Chef, String> {
}

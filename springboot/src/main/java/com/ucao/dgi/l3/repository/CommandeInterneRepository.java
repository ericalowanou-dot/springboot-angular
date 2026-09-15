package com.ucao.dgi.l3.repository;

import com.ucao.dgi.l3.entity.CommandeInterne;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommandeInterneRepository extends JpaRepository<CommandeInterne, Integer> {
    List<CommandeInterne> findAllByFournisseurIdFournisseur(Integer idFournisseur);

}

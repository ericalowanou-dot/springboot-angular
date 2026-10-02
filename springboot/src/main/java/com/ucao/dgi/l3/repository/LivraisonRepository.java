package com.ucao.dgi.l3.repository;

import com.ucao.dgi.l3.entity.Livraison;
import com.ucao.dgi.l3.entity.Personnel;
import com.ucao.dgi.l3.entity.StatutCommande;
import com.ucao.dgi.l3.entity.StatutLivraison;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LivraisonRepository extends JpaRepository<Livraison, Integer> {

    Livraison findByCommandeIdCommande(Integer idCommande);

    List<Livraison> findAllByDateLivraison(LocalDate dateLivraison);

    List<Livraison> findAllByLivreurIdPersonnel(Integer idPersonnel, Sort sort);

    /** Courses prêtes que personne n'a encore prises : visibles par tous les livreurs. */
    @Query("SELECT l FROM Livraison l WHERE l.livreur IS NULL AND l.statut = :aAssigner "
            + "AND l.commande.statut = :prete ORDER BY l.idLivraison")
    List<Livraison> findDisponibles(@Param("aAssigner") StatutLivraison aAssigner, @Param("prete") StatutCommande prete);

    /**
     * Attribution atomique : ne réussit (1 ligne modifiée) que si la course est encore libre.
     * Si deux livreurs appuient en même temps, la base garantit qu'un seul l'obtient.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Livraison l SET l.livreur = :livreur, l.statut = :assignee "
            + "WHERE l.idLivraison = :id AND l.livreur IS NULL AND l.statut = :aAssigner")
    int prendre(@Param("id") Integer id, @Param("livreur") Personnel livreur,
                @Param("assignee") StatutLivraison assignee, @Param("aAssigner") StatutLivraison aAssigner);
}

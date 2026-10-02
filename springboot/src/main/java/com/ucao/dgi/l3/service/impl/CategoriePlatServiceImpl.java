package com.ucao.dgi.l3.service.impl;

import com.ucao.dgi.l3.entity.CategoriePlat;
import com.ucao.dgi.l3.exception.RegleMetierException;
import com.ucao.dgi.l3.exception.RessourceIntrouvableException;
import com.ucao.dgi.l3.repository.CategoriePlatRepository;
import com.ucao.dgi.l3.service.CategoriePlatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoriePlatServiceImpl implements CategoriePlatService {

    private final CategoriePlatRepository categoriePlatRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoriePlat> findAll() {
        return categoriePlatRepository.findAll(Sort.by("nom"));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriePlat findById(Integer id) {
        return categoriePlatRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Catégorie", id));
    }

    @Override
    public CategoriePlat save(CategoriePlat categorie) {
        categorie.setIdCategorie(null);
        return categoriePlatRepository.save(categorie);
    }

    @Override
    public CategoriePlat update(Integer id, CategoriePlat donnees) {
        CategoriePlat categorie = findById(id);
        categorie.setNom(donnees.getNom());
        categorie.setDescription(donnees.getDescription());
        return categorie;
    }

    @Override
    public void delete(Integer id) {
        CategoriePlat categorie = findById(id);
        if (categorie.getNombrePlats() > 0) {
            throw new RegleMetierException("Impossible de supprimer la catégorie « " + categorie.getNom()
                    + " » : elle contient encore " + categorie.getNombrePlats() + " plat(s)");
        }
        categoriePlatRepository.delete(categorie);
    }
}

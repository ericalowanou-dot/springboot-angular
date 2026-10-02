package com.ucao.dgi.l3.service.impl;

import com.ucao.dgi.l3.entity.CategoriePlat;
import com.ucao.dgi.l3.entity.Plat;
import com.ucao.dgi.l3.exception.RegleMetierException;
import com.ucao.dgi.l3.exception.RessourceIntrouvableException;
import com.ucao.dgi.l3.repository.CategoriePlatRepository;
import com.ucao.dgi.l3.repository.PlatRepository;
import com.ucao.dgi.l3.service.ImageUploadService;
import com.ucao.dgi.l3.service.PlatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class PlatServiceImpl implements PlatService {

    private final PlatRepository platRepository;
    private final CategoriePlatRepository categoriePlatRepository;
    private final ImageUploadService imageUploadService;

    @Override
    @Transactional(readOnly = true)
    public List<Plat> findAll(Integer categorieId) {
        if (categorieId != null) {
            return platRepository.findByCategorieIdCategorie(categorieId);
        }
        return platRepository.findAll(Sort.by("nom"));
    }

    @Override
    @Transactional(readOnly = true)
    public Plat findById(Integer id) {
        return platRepository.findById(id).orElseThrow(() -> new RessourceIntrouvableException("Plat", id));
    }

    @Override
    public Plat save(Plat plat) {
        plat.setIdPlat(null);
        plat.setCategorie(resoudreCategorie(plat.getCategorie()));
        if (plat.getDisponible() == null) {
            plat.setDisponible(true);
        }
        return platRepository.save(plat);
    }

    @Override
    public Plat update(Integer id, Plat donnees) {
        Plat plat = findById(id);
        if (!Objects.equals(plat.getImageUrl(), donnees.getImageUrl())) {
            imageUploadService.supprimerSiInterne(plat.getImageUrl());
        }
        plat.setNom(donnees.getNom());
        plat.setPrix(donnees.getPrix());
        plat.setDescription(donnees.getDescription());
        plat.setImageUrl(donnees.getImageUrl());
        plat.setDisponible(donnees.getDisponible() == null || donnees.getDisponible());
        plat.setCategorie(resoudreCategorie(donnees.getCategorie()));
        return plat;
    }

    @Override
    public Plat changerDisponibilite(Integer id, boolean disponible) {
        Plat plat = findById(id);
        plat.setDisponible(disponible);
        return plat;
    }

    @Override
    public void delete(Integer id) {
        Plat plat = findById(id);
        if (plat.getLignes() != null && !plat.getLignes().isEmpty()) {
            throw new RegleMetierException("Ce plat figure dans des commandes : rendez-le plutôt indisponible");
        }
        imageUploadService.supprimerSiInterne(plat.getImageUrl());
        platRepository.delete(plat);
        platRepository.flush(); // un plat encore présent dans un menu provoque une violation de clé étrangère
    }

    private CategoriePlat resoudreCategorie(CategoriePlat categorie) {
        if (categorie == null || categorie.getIdCategorie() == null) {
            return null;
        }
        return categoriePlatRepository.findById(categorie.getIdCategorie())
                .orElseThrow(() -> new RessourceIntrouvableException("Catégorie", categorie.getIdCategorie()));
    }
}

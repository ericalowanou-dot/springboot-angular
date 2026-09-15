package com.ucao.dgi.l3.service.impl;

import com.ucao.dgi.l3.entity.CategoriePlat;
import com.ucao.dgi.l3.entity.Plat;
import com.ucao.dgi.l3.repository.CategoriePlatRepository;
import com.ucao.dgi.l3.service.CategoriePlatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriePlatServiceImpl implements CategoriePlatService {

    @Autowired
    private CategoriePlatRepository categoriePlatRepository;

    @Override
    public List<CategoriePlat> findAll() {
        return categoriePlatRepository.findAll();
    }

    @Override
    public CategoriePlat save(CategoriePlat categoriePlat) {
        return categoriePlatRepository.save(categoriePlat);
    }

    @Override
    public CategoriePlat update(CategoriePlat categoriePlat) {
        return categoriePlatRepository.save(categoriePlat);
    }

    @Override
    public CategoriePlat delete(CategoriePlat categoriePlat) {
        categoriePlatRepository.delete(categoriePlat);
        return categoriePlat;
    }

    @Override
    public CategoriePlat findById(Integer id) {
        return categoriePlatRepository.findById(id).orElse(null);
    }

    @Override
    public CategoriePlat findByNomCategorie(String nom) {
        return categoriePlatRepository.findByNom(nom);
    }

    @Override
    public List<Plat> findAllPlatsInCategorie(Integer idCategorie) {
        CategoriePlat categorie = categoriePlatRepository.findById(idCategorie).orElse(null);
        if (categorie == null) return null;
        return categorie.getPlats();
    }

}

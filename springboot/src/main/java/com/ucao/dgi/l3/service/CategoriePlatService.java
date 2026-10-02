package com.ucao.dgi.l3.service;

import com.ucao.dgi.l3.entity.CategoriePlat;

import java.util.List;

public interface CategoriePlatService {
    List<CategoriePlat> findAll();

    CategoriePlat findById(Integer id);

    CategoriePlat save(CategoriePlat categorie);

    CategoriePlat update(Integer id, CategoriePlat categorie);

    void delete(Integer id);
}

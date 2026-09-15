package com.ucao.dgi.l3.service;

import com.ucao.dgi.l3.entity.CategoriePlat;
import com.ucao.dgi.l3.entity.Plat;

import java.util.List;

public interface CategoriePlatService {
    public List<CategoriePlat> findAll();
    public CategoriePlat save(CategoriePlat categoriePlat);
    public CategoriePlat update(CategoriePlat categoriePlat);
    public CategoriePlat delete(CategoriePlat categoriePlat);
    public CategoriePlat findById(Integer id);

    public CategoriePlat findByNomCategorie(String nomCategorie);

    List<Plat> findAllPlatsInCategorie(Integer idCategorie);



}

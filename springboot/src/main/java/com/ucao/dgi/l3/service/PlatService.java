package com.ucao.dgi.l3.service;

import com.ucao.dgi.l3.entity.Plat;

import java.util.List;

public interface PlatService {
    List<Plat> findAll(Integer categorieId);

    Plat findById(Integer id);

    Plat save(Plat plat);

    Plat update(Integer id, Plat plat);

    Plat changerDisponibilite(Integer id, boolean disponible);

    void delete(Integer id);
}

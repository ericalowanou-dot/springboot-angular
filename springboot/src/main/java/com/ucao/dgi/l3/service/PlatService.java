package com.ucao.dgi.l3.service;

import com.ucao.dgi.l3.entity.Plat;

import java.util.List;

public interface PlatService {
    public List<Plat> findAll();
    public Plat save(Plat plat);
    public Plat findById(Integer idPlat);
    public Plat update(Plat plat);
    public Plat delete(Plat plat);

    public Plat findByCaterory(Integer idCategorie);
    public Plat findByNomPlat(String nomPlat);
    public Plat findPlatBetween(Double min,  Double max);
}

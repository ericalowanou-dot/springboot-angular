package com.ucao.dgi.l3.service.impl;

import com.ucao.dgi.l3.entity.Plat;
import com.ucao.dgi.l3.repository.PlatRepository;
import com.ucao.dgi.l3.service.PlatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlatServiceImpl implements PlatService {

    @Autowired
    private PlatRepository platRepository;

    @Override
    public List<Plat> findAll() {
        return platRepository.findAll();
    }

    @Override
    public Plat save(Plat plat) {
        return platRepository.save(plat);
    }

    @Override
    public Plat findById(Integer idPlat) {
        return platRepository.findById(idPlat).orElse(null);
    }

    @Override
    public Plat update(Plat plat) {
        return platRepository.save(plat);
    }

    @Override
    public Plat delete(Plat plat) {
        platRepository.delete(plat);
        return plat;
    }

    @Override
    public Plat findByCaterory(Integer idCategorie) {
        List<Plat> plats = platRepository.findByCategorieIdCategorie(idCategorie);
        return plats != null && !plats.isEmpty() ? plats.get(0) : null;
    }

    @Override
    public Plat findByNomPlat(String nomPlat) {
        return platRepository.findByNom(nomPlat);
    }

    @Override
    public Plat findPlatBetween(Double min, Double max) {
        return platRepository.findPlatByPrixBetween(min, max);
    }
}

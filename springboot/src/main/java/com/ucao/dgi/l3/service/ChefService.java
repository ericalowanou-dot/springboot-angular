package com.ucao.dgi.l3.service;

import com.ucao.dgi.l3.entity.Chef;

import java.util.List;

public interface ChefService {

    List<Chef> findAllChefs();

    Chef findChefById(String id);

    Chef saveChef(Chef chef);

    Chef updateChef(Chef chef);

    Chef deleteChef(String idChef);
}

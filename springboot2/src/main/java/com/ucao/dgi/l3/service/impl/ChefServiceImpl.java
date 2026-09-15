package com.ucao.dgi.l3.service.impl;

import com.ucao.dgi.l3.entity.Chef;
import com.ucao.dgi.l3.repository.ChefRepository;
import com.ucao.dgi.l3.service.ChefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChefServiceImpl implements ChefService {

    @Autowired
    private ChefRepository chefRepository;

    @Override
    public List<Chef> findAllChefs() {
        return chefRepository.findAll();
    }

    @Override
    public Chef findChefById(String id) {
        return chefRepository.findById(id).orElse(null);
    }

    @Override
    public Chef saveChef(Chef chef) {
        return chefRepository.save(chef);
    }

    @Override
    public Chef updateChef(Chef chef) {
        return chefRepository.save(chef);
    }

    @Override
    public Chef deleteChef(String idChef) {
        Chef chef = chefRepository.findById(idChef).orElse(null);
        if (chef != null) {
            chefRepository.delete(chef);
        }
        return chef;
    }
}

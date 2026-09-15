package com.ucao.dgi.l3.service.impl;

import com.ucao.dgi.l3.entity.Serveur;
import com.ucao.dgi.l3.repository.ServeurRepository;
import com.ucao.dgi.l3.service.ServeurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServeurServiceImpl implements ServeurService {

    @Autowired
    private ServeurRepository serveurRepository;

    @Override
    public List<Serveur> findAllServeurs() {
        return serveurRepository.findAll();
    }

    @Override
    public Serveur findServeurById(String id) {
        return serveurRepository.findById(id).orElse(null);
    }

    @Override
    public Serveur saveServeur(Serveur serveur) {
        return serveurRepository.save(serveur);
    }

    @Override
    public Serveur updateServeur(Serveur serveur) {
        return serveurRepository.save(serveur);
    }

    @Override
    public Serveur deleteServeur(String idServeur) {
        Serveur serveur = serveurRepository.findById(idServeur).orElse(null);
        if (serveur != null) {
            serveurRepository.delete(serveur);
        }
        return serveur;
    }
}

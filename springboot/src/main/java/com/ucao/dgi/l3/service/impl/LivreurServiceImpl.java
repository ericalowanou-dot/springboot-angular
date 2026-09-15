package com.ucao.dgi.l3.service.impl;

import com.ucao.dgi.l3.entity.Livreur;
import com.ucao.dgi.l3.repository.LivreurRepository;
import com.ucao.dgi.l3.service.LivreurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LivreurServiceImpl implements LivreurService {

    @Autowired
    private LivreurRepository livreurRepository;

    @Override
    public List<Livreur> findAllLivreurs() {
        return livreurRepository.findAll();
    }

    @Override
    public Livreur findLivreurById(String id) {
        return livreurRepository.findById(id).orElse(null);
    }

    @Override
    public Livreur saveLivreur(Livreur livreur) {
        return livreurRepository.save(livreur);
    }

    @Override
    public Livreur updateLivreur(Livreur livreur) {
        return livreurRepository.save(livreur);
    }

    @Override
    public Livreur deleteLivreur(String idLivreur) {
        Livreur livreur = livreurRepository.findById(idLivreur).orElse(null);
        if (livreur != null) {
            livreurRepository.delete(livreur);
        }
        return livreur;
    }
}

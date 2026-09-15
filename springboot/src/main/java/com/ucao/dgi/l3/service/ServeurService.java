package com.ucao.dgi.l3.service;

import com.ucao.dgi.l3.entity.Serveur;

import java.util.List;

public interface ServeurService {

    List<Serveur> findAllServeurs();

    Serveur findServeurById(String id);

    Serveur saveServeur(Serveur serveur);

    Serveur updateServeur(Serveur serveur);

    Serveur deleteServeur(String idServeur);
}

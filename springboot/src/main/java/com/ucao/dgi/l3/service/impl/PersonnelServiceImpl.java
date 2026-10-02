package com.ucao.dgi.l3.service.impl;

import com.ucao.dgi.l3.entity.Personnel;
import com.ucao.dgi.l3.exception.RegleMetierException;
import com.ucao.dgi.l3.exception.RessourceIntrouvableException;
import com.ucao.dgi.l3.repository.PersonnelRepository;
import com.ucao.dgi.l3.repository.UserRepository;
import com.ucao.dgi.l3.service.PersonnelService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PersonnelServiceImpl implements PersonnelService {

    private final PersonnelRepository personnelRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Personnel> findAll(String fonction) {
        if (fonction != null && !fonction.isBlank()) {
            return personnelRepository.findAllByFonctionIgnoreCase(fonction);
        }
        return personnelRepository.findAll(Sort.by("nom", "prenom"));
    }

    @Override
    @Transactional(readOnly = true)
    public Personnel findById(Integer id) {
        return personnelRepository.findById(id).orElseThrow(() -> new RessourceIntrouvableException("Employé", id));
    }

    @Override
    public Personnel save(Personnel personnel) {
        personnel.setIdPersonnel(null);
        personnel.setFonction(normaliserFonction(personnel.getFonction()));
        if (personnel.getActif() == null) {
            personnel.setActif(true);
        }
        return personnelRepository.save(personnel);
    }

    @Override
    public Personnel update(Integer id, Personnel donnees) {
        Personnel p = findById(id);
        p.setNom(donnees.getNom());
        p.setPrenom(donnees.getPrenom());
        p.setFonction(normaliserFonction(donnees.getFonction()));
        p.setTelephone(donnees.getTelephone());
        p.setEmail(donnees.getEmail());
        p.setSalaire(donnees.getSalaire());
        p.setDateEmbauche(donnees.getDateEmbauche());
        p.setActif(donnees.getActif() == null || donnees.getActif());
        // le compte de connexion éventuel suit la fiche
        userRepository.findByPersonnelIdPersonnel(id).ifPresent(compte -> {
            if (!"LIVREUR".equals(p.getFonction())) {
                throw new RegleMetierException("Cet employé a un accès livreur : supprimez-le avant de changer sa fonction");
            }
            compte.setNom(p.getNom());
            compte.setPrenom(p.getPrenom());
            compte.setTelephone(p.getTelephone());
        });
        return p;
    }

    @Override
    public void delete(Integer id) {
        Personnel p = findById(id);
        // l'accès livreur éventuel disparaît avec la fiche (même transaction)
        userRepository.findByPersonnelIdPersonnel(id).ifPresent(userRepository::delete);
        personnelRepository.delete(p);
        personnelRepository.flush(); // remonte immédiatement une éventuelle violation de clé étrangère
    }

    private String normaliserFonction(String fonction) {
        String f = fonction == null ? "" : fonction.trim().toUpperCase();
        if (!Personnel.FONCTIONS.contains(f)) {
            throw new RegleMetierException("Fonction inconnue : " + fonction);
        }
        return f;
    }
}

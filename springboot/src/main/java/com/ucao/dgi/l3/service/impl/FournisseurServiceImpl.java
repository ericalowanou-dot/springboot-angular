package com.ucao.dgi.l3.service.impl;

import com.ucao.dgi.l3.entity.Fournisseur;
import com.ucao.dgi.l3.exception.RegleMetierException;
import com.ucao.dgi.l3.exception.RessourceIntrouvableException;
import com.ucao.dgi.l3.repository.FournisseurRepository;
import com.ucao.dgi.l3.service.FournisseurService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FournisseurServiceImpl implements FournisseurService {

    private final FournisseurRepository fournisseurRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Fournisseur> findAll() {
        return fournisseurRepository.findAll(Sort.by("nom"));
    }

    @Override
    @Transactional(readOnly = true)
    public Fournisseur findById(Integer id) {
        return fournisseurRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Fournisseur", id));
    }

    @Override
    public Fournisseur save(Fournisseur fournisseur) {
        fournisseur.setIdFournisseur(null);
        return fournisseurRepository.save(fournisseur);
    }

    @Override
    public Fournisseur update(Integer id, Fournisseur donnees) {
        Fournisseur f = findById(id);
        f.setNom(donnees.getNom());
        f.setContact(donnees.getContact());
        f.setTelephone(donnees.getTelephone());
        f.setEmail(donnees.getEmail());
        f.setAdresse(donnees.getAdresse());
        return f;
    }

    @Override
    public void delete(Integer id) {
        Fournisseur f = findById(id);
        boolean utilise = (f.getProduits() != null && !f.getProduits().isEmpty())
                || (f.getCommandesInternes() != null && !f.getCommandesInternes().isEmpty());
        if (utilise) {
            throw new RegleMetierException("Ce fournisseur a des produits ou des commandes : suppression impossible");
        }
        fournisseurRepository.delete(f);
    }
}

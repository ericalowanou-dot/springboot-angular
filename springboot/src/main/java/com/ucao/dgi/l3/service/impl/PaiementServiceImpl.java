package com.ucao.dgi.l3.service.impl;

import com.ucao.dgi.l3.entity.Paiement;
import com.ucao.dgi.l3.repository.PaiementRepository;
import com.ucao.dgi.l3.service.PaiementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PaiementServiceImpl implements PaiementService {

    @Autowired
    private PaiementRepository paiementRepository;

    @Override
    public List<Paiement> findAllPaiements() {
        return paiementRepository.findAll();
    }

    @Override
    public Paiement findPaiementById(Integer idPaiement) {
        return paiementRepository.findById(idPaiement).orElse(null);
    }

    @Override
    public Paiement savePaiement(Paiement paiement) {
        return paiementRepository.save(paiement);
    }

    @Override
    public Paiement updatePaiement(Paiement paiement) {
        return paiementRepository.save(paiement);
    }

    @Override
    public Paiement deletePaiement(Paiement paiement) {
        paiementRepository.delete(paiement);
        return paiement;
    }

    @Override
    public Paiement findByCommandeId(Integer idCommande) {
        return paiementRepository.findByCommandeIdCommande(idCommande);
    }

    @Override
    public Paiement findByDatePaiement(LocalDate datePaiement) {
        return paiementRepository.findByDatePaiement(datePaiement);
    }

    @Override
    public Paiement findByMethode(String methode) {
        return paiementRepository.findByMethode(methode);
    }
}

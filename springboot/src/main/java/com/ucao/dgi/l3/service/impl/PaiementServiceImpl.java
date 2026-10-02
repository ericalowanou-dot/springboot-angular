package com.ucao.dgi.l3.service.impl;

import com.ucao.dgi.l3.entity.Paiement;
import com.ucao.dgi.l3.repository.PaiementRepository;
import com.ucao.dgi.l3.service.PaiementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Les paiements sont créés via CommandeService.payer ; ce service sert l'historique. */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaiementServiceImpl implements PaiementService {

    private final PaiementRepository paiementRepository;

    @Override
    public List<Paiement> findAll() {
        return paiementRepository.findAll(Sort.by(Sort.Direction.DESC, "idPaiement"));
    }
}

package com.ucao.dgi.l3.controller;

import com.ucao.dgi.l3.entity.Paiement;
import com.ucao.dgi.l3.service.PaiementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Historique des encaissements. Un paiement se crée via POST /api/commandes/{id}/paiement. */
@RestController
@RequestMapping("/api/paiements")
@RequiredArgsConstructor
public class PaiementController {

    private final PaiementService paiementService;

    @GetMapping
    public List<Paiement> findAll() {
        return paiementService.findAll();
    }
}

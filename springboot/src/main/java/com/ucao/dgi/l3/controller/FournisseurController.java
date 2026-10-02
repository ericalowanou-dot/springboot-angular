package com.ucao.dgi.l3.controller;

import com.ucao.dgi.l3.entity.Fournisseur;
import com.ucao.dgi.l3.service.FournisseurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fournisseurs")
@RequiredArgsConstructor
public class FournisseurController {

    private final FournisseurService fournisseurService;

    @GetMapping
    public List<Fournisseur> findAll() {
        return fournisseurService.findAll();
    }

    @GetMapping("/{id}")
    public Fournisseur findById(@PathVariable Integer id) {
        return fournisseurService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Fournisseur save(@Valid @RequestBody Fournisseur fournisseur) {
        return fournisseurService.save(fournisseur);
    }

    @PutMapping("/{id}")
    public Fournisseur update(@PathVariable Integer id, @Valid @RequestBody Fournisseur fournisseur) {
        return fournisseurService.update(id, fournisseur);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        fournisseurService.delete(id);
    }
}

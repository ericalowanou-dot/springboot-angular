package com.ucao.dgi.l3.controller;

import com.ucao.dgi.l3.entity.Plat;
import com.ucao.dgi.l3.service.PlatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plats")
@RequiredArgsConstructor
public class PlatController {

    private final PlatService platService;

    @GetMapping
    public List<Plat> findAll(@RequestParam(required = false) Integer categorieId) {
        return platService.findAll(categorieId);
    }

    @GetMapping("/{id}")
    public Plat findById(@PathVariable Integer id) {
        return platService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Plat save(@Valid @RequestBody Plat plat) {
        return platService.save(plat);
    }

    @PutMapping("/{id}")
    public Plat update(@PathVariable Integer id, @Valid @RequestBody Plat plat) {
        return platService.update(id, plat);
    }

    @PatchMapping("/{id}/disponibilite")
    public Plat changerDisponibilite(@PathVariable Integer id, @RequestParam boolean disponible) {
        return platService.changerDisponibilite(id, disponible);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        platService.delete(id);
    }
}

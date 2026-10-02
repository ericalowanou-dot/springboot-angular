package com.ucao.dgi.l3.controller;

import com.ucao.dgi.l3.entity.CategoriePlat;
import com.ucao.dgi.l3.service.CategoriePlatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoriePlatController {

    private final CategoriePlatService categoriePlatService;

    @GetMapping
    public List<CategoriePlat> findAll() {
        return categoriePlatService.findAll();
    }

    @GetMapping("/{id}")
    public CategoriePlat findById(@PathVariable Integer id) {
        return categoriePlatService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoriePlat save(@Valid @RequestBody CategoriePlat categorie) {
        return categoriePlatService.save(categorie);
    }

    @PutMapping("/{id}")
    public CategoriePlat update(@PathVariable Integer id, @Valid @RequestBody CategoriePlat categorie) {
        return categoriePlatService.update(id, categorie);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        categoriePlatService.delete(id);
    }
}

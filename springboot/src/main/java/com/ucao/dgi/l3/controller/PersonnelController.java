package com.ucao.dgi.l3.controller;

import com.ucao.dgi.l3.entity.Personnel;
import com.ucao.dgi.l3.service.PersonnelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personnel")
@RequiredArgsConstructor
public class PersonnelController {

    private final PersonnelService personnelService;

    /** ?fonction=LIVREUR permet de filtrer (ex. pour assigner une livraison). */
    @GetMapping
    public List<Personnel> findAll(@RequestParam(required = false) String fonction) {
        return personnelService.findAll(fonction);
    }

    @GetMapping("/{id}")
    public Personnel findById(@PathVariable Integer id) {
        return personnelService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Personnel save(@Valid @RequestBody Personnel personnel) {
        return personnelService.save(personnel);
    }

    @PutMapping("/{id}")
    public Personnel update(@PathVariable Integer id, @Valid @RequestBody Personnel personnel) {
        return personnelService.update(id, personnel);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        personnelService.delete(id);
    }
}

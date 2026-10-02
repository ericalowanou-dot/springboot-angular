package com.ucao.dgi.l3.controller;

import com.ucao.dgi.l3.dto.AuthDtos.AccesLivreurRequest;
import com.ucao.dgi.l3.dto.AuthDtos.UserDto;
import com.ucao.dgi.l3.entity.Personnel;
import com.ucao.dgi.l3.service.PersonnelService;
import com.ucao.dgi.l3.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/personnel")
@RequiredArgsConstructor
public class PersonnelController {

    private final PersonnelService personnelService;
    private final UserService userService;

    /** ?fonction=LIVREUR permet de filtrer (ex. pour assigner une livraison). */
    @GetMapping
    public List<Personnel> findAll(@RequestParam(required = false) String fonction) {
        return personnelService.findAll(fonction);
    }

    /** Comptes de connexion rattachés aux fiches : { idPersonnel: utilisateur }. */
    @GetMapping("/acces")
    public Map<Integer, UserDto> acces() {
        return userService.accesPersonnel();
    }

    @PostMapping("/{id}/acces")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto creerAcces(@PathVariable Integer id, @Valid @RequestBody AccesLivreurRequest requete) {
        return userService.creerAccesLivreur(personnelService.findById(id), requete);
    }

    @PutMapping("/{id}/acces")
    public UserDto modifierAcces(@PathVariable Integer id, @Valid @RequestBody AccesLivreurRequest requete) {
        return userService.modifierAccesLivreur(id, requete);
    }

    @DeleteMapping("/{id}/acces")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimerAcces(@PathVariable Integer id) {
        userService.supprimerAccesLivreur(id);
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

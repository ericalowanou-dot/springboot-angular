package com.ucao.dgi.l3.controller;

import com.ucao.dgi.l3.dto.PersonnelDTO;
import com.ucao.dgi.l3.entity.Personnel;
import com.ucao.dgi.l3.service.impl.PersonnelServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/personnels")
@CrossOrigin("*")
@RestController
public class PersonnelController {

    @Autowired
    private PersonnelServiceImpl personnelServiceImpl;

    @GetMapping("/find_all")
    public List<Personnel> findAll() {
        return personnelServiceImpl.findAllPersonnel();
    }

    @GetMapping("/find_by_id/{idPersonnel}")
    public Personnel findById(@PathVariable Integer idPersonnel) {
        return personnelServiceImpl.findPersonnelById(idPersonnel);
    }

    @PostMapping("/save")
    public Personnel save(@RequestBody PersonnelDTO personnelDTO) {
        return personnelServiceImpl.savePersonnel(personnelDTO);
    }

    @PostMapping("/update")
    public Personnel update(@RequestBody PersonnelDTO personnelDTO) {
        return personnelServiceImpl.updatePersonnel(personnelDTO);
    }

    @PutMapping("/update/{idPersonnel}")
    public Personnel updateById(@PathVariable Integer idPersonnel, @RequestBody PersonnelDTO personnelDTO) {
        personnelDTO.setIdPersonnel(idPersonnel);
        return personnelServiceImpl.updatePersonnel(personnelDTO);
    }

    @PostMapping("/delete/{idPersonnel}")
    public Personnel delete(@PathVariable Integer idPersonnel) {
        return personnelServiceImpl.deletePersonnel(idPersonnel);
    }

    @GetMapping("/find_serveur")
    public Personnel findPersonnelServeur() {
        return personnelServiceImpl.findPersonnelServeur();
    }

    @GetMapping("/find_livreur")
    public Personnel findPersonnelLivreur() {
        return personnelServiceImpl.findPersonnelLivreur();
    }

    @GetMapping("/find_chef")
    public Personnel findPersonnelChef() {
        return personnelServiceImpl.findPersonnelChef();
    }
}

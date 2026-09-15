package com.ucao.dgi.l3.controller;

import com.ucao.dgi.l3.entity.Chef;
import com.ucao.dgi.l3.service.impl.ChefServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/chefs")
@CrossOrigin("*")
@RestController
public class ChefController {

    @Autowired
    private ChefServiceImpl chefServiceImpl;

    @GetMapping("/find_all")
    public List<Chef> findAll() {
        return chefServiceImpl.findAllChefs();
    }

    @GetMapping("/find_by_id/{id}")
    public Chef findById(@PathVariable String id) {
        return chefServiceImpl.findChefById(id);
    }

    @PostMapping("/save")
    public Chef save(@RequestBody Chef chef) {
        return chefServiceImpl.saveChef(chef);
    }

    @PostMapping("/update")
    public Chef update(@RequestBody Chef chef) {
        return chefServiceImpl.updateChef(chef);
    }

    @PostMapping("/delete/{idChef}")
    public Chef delete(@PathVariable String idChef) {
        return chefServiceImpl.deleteChef(idChef);
    }
}


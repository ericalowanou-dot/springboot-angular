package com.ucao.dgi.l3.controller;

import com.ucao.dgi.l3.entity.Menu;
import com.ucao.dgi.l3.service.impl.MenuServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/menus")
@CrossOrigin("*")
@RestController
public class MenuController {

    @Autowired
    private MenuServiceImpl menuServiceImpl;

    @GetMapping("/find_all")
    public List<Menu> findAll() {
        return menuServiceImpl.findAll();
    }

    @GetMapping("/find_by_id/{id}")
    public Menu findById(@PathVariable Integer id) {
        return menuServiceImpl.findById(id);
    }

    @PostMapping("/save")
    public Menu save(@RequestBody Menu menu) {
        return menuServiceImpl.save(menu);
    }

    @PutMapping("/update/{id}")
    public Menu update(@PathVariable Integer id, @RequestBody Menu menu) {
        menu.setIdMenu(id.longValue());
        return menuServiceImpl.update(menu);
    }

    @DeleteMapping("/delete/{id}")
    public Menu delete(@PathVariable Integer id) {
        Menu menu = new Menu();
        menu.setIdMenu(id.longValue());
        return menuServiceImpl.delete(menu);
    }

    @GetMapping("/find_by_nom/{nomMenu}")
    public Menu findByNomMenu(@PathVariable String nomMenu) {
        return menuServiceImpl.findByNomMenu(nomMenu);
    }

    @GetMapping("/find_by_categorie/{idCategorie}")
    public List<Menu> findByCategorie(@PathVariable Integer idCategorie) {
        return menuServiceImpl.findByCategorie(idCategorie);
    }
}

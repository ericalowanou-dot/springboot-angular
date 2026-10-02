package com.ucao.dgi.l3.controller;

import com.ucao.dgi.l3.entity.Menu;
import com.ucao.dgi.l3.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    public List<Menu> findAll() {
        return menuService.findAll();
    }

    @GetMapping("/{id}")
    public Menu findById(@PathVariable Long id) {
        return menuService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Menu save(@Valid @RequestBody Menu menu) {
        return menuService.save(menu);
    }

    @PutMapping("/{id}")
    public Menu update(@PathVariable Long id, @Valid @RequestBody Menu menu) {
        return menuService.update(id, menu);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        menuService.delete(id);
    }
}

package com.ucao.dgi.l3.service;

import com.ucao.dgi.l3.entity.Menu;

import java.util.List;

public interface MenuService {
    List<Menu> findAll();

    Menu findById(Long id);

    Menu save(Menu menu);

    Menu update(Long id, Menu menu);

    void delete(Long id);
}

package com.ucao.dgi.l3.service;

import com.ucao.dgi.l3.entity.Menu;
import java.util.List;

public interface MenuService {

    List<Menu> findAll();

    Menu findById(Integer id);

    Menu save(Menu menu);

    Menu update(Menu menu);

    Menu delete(Menu menu);

    Menu findByNomMenu(String nomMenu);

    List<Menu> findByCategorie(Integer idCategorie);
}

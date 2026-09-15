package com.ucao.dgi.l3.service.impl;

import com.ucao.dgi.l3.entity.Menu;
import com.ucao.dgi.l3.repository.MenuRepository;
import com.ucao.dgi.l3.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuRepository menuRepository;

    @Override
    public List<Menu> findAll() {
        return menuRepository.findAll();
    }

    @Override
    public Menu findById(Integer id) {
        return menuRepository.findById(id).orElse(null);
    }

    @Override
    public Menu save(Menu menu) {
        return menuRepository.save(menu);
    }

    @Override
    public Menu update(Menu menu) {
        return menuRepository.save(menu);
    }

    @Override
    public Menu delete(Menu menu) {
        menuRepository.delete(menu);
        return menu;
    }

    @Override
    public Menu findByNomMenu(String nomMenu) {
        return menuRepository.findByNom(nomMenu);
    }

    @Override
    public List<Menu> findByCategorie(Integer idCategorie) {
        // Note: Menu n'a pas de relation directe avec CategoriePlat
        // Cette méthode retourne une liste vide car la relation passe par Plat (ManyToMany)
        // Pour trouver les menus d'une catégorie, il faudrait filtrer via les plats
        return List.of();
    }
}

package com.ucao.dgi.l3.service.impl;

import com.ucao.dgi.l3.entity.Menu;
import com.ucao.dgi.l3.entity.Plat;
import com.ucao.dgi.l3.exception.RessourceIntrouvableException;
import com.ucao.dgi.l3.repository.MenuRepository;
import com.ucao.dgi.l3.repository.PlatRepository;
import com.ucao.dgi.l3.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final PlatRepository platRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Menu> findAll() {
        return menuRepository.findAll(Sort.by("nom"));
    }

    @Override
    @Transactional(readOnly = true)
    public Menu findById(Long id) {
        return menuRepository.findById(id).orElseThrow(() -> new RessourceIntrouvableException("Menu", id));
    }

    @Override
    public Menu save(Menu menu) {
        menu.setIdMenu(null);
        menu.setPlats(resoudrePlats(menu.getPlats()));
        return menuRepository.save(menu);
    }

    @Override
    public Menu update(Long id, Menu donnees) {
        Menu menu = findById(id);
        menu.setNom(donnees.getNom());
        menu.setDescription(donnees.getDescription());
        menu.setPrix(donnees.getPrix());
        menu.setPlats(resoudrePlats(donnees.getPlats()));
        return menu;
    }

    @Override
    public void delete(Long id) {
        menuRepository.delete(findById(id));
    }

    /** Le frontend n'envoie que les identifiants des plats : on recharge les entités. */
    private List<Plat> resoudrePlats(List<Plat> plats) {
        if (plats == null) {
            return new ArrayList<>();
        }
        List<Integer> ids = plats.stream().map(Plat::getIdPlat).filter(Objects::nonNull).distinct().toList();
        List<Plat> trouves = platRepository.findAllById(ids);
        if (trouves.size() != ids.size()) {
            throw new RessourceIntrouvableException("Plat", ids);
        }
        return new ArrayList<>(trouves);
    }
}

package com.ucao.dgi.l3.service;

import com.ucao.dgi.l3.entity.Personnel;

import java.util.List;

public interface PersonnelService {
    List<Personnel> findAll(String fonction);

    Personnel findById(Integer id);

    Personnel save(Personnel personnel);

    Personnel update(Integer id, Personnel personnel);

    void delete(Integer id);
}

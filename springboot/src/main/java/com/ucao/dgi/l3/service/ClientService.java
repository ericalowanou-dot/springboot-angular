package com.ucao.dgi.l3.service;

import com.ucao.dgi.l3.entity.Client;

import java.util.List;

public interface ClientService {
    List<Client> findAll();

    Client findById(Integer id);

    Client save(Client client);

    Client update(Integer id, Client client);

    void delete(Integer id);
}

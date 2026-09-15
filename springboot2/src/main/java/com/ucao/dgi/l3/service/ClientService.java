package com.ucao.dgi.l3.service;

import com.ucao.dgi.l3.entity.Client;
import com.ucao.dgi.l3.entity.Commande;

import java.util.List;

public interface ClientService {
    public List<Client> findAll();
    public Client findById(Integer id);
    public Client save(Client client);
    public Client delete(Client idClient);
    public Client update(Client client);

    List<Commande> findAllCommandeByClient(Integer idClient);
}

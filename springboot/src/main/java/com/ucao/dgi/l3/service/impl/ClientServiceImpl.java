package com.ucao.dgi.l3.service.impl;

import com.ucao.dgi.l3.entity.Client;
import com.ucao.dgi.l3.entity.Commande;
import com.ucao.dgi.l3.repository.ClientRepository;
import com.ucao.dgi.l3.repository.CommandeRepository;
import com.ucao.dgi.l3.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    ClientRepository clientRepository;
    @Autowired
    CommandeRepository commandeRepository;

    @Override
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    @Override
    public Client findById(Integer id) {
        return clientRepository.findById(id).orElse(null);
    }

    @Override
    public Client save(Client client) {
        return clientRepository.save(client);
    }

    @Override
    public Client delete(Client idClient) {
        clientRepository.delete(idClient);
        return idClient;
    }

    @Override
    public Client update(Client client) {
        return clientRepository.save(client);
    }

    @Override
    public List<Commande> findAllCommandeByClient(Integer idClient) {
        return commandeRepository.findAllByClientIdClient(idClient);
    }
}

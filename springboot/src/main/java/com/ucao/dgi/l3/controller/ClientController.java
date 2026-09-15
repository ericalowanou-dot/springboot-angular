package com.ucao.dgi.l3.controller;

import com.ucao.dgi.l3.entity.Client;
import com.ucao.dgi.l3.entity.Commande;
import com.ucao.dgi.l3.service.impl.ClientServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/clients")
@CrossOrigin("*")
@RestController

public class ClientController {
    @Autowired
    private ClientServiceImpl clientServiceImpl;

    @GetMapping("/find_by_id/{idClient}")
    public Client FindById(@PathVariable Integer idClient) {
        return clientServiceImpl.findById(idClient);
    }

    @PostMapping("/save")
    public Client Save(@RequestBody Client client) {
        return clientServiceImpl.save(client);
    }

    @PutMapping("/update/{idClient}")
    public Client Update(@PathVariable Integer idClient, @RequestBody Client client) {
        client.setIdClient(idClient);
        return clientServiceImpl.update(client);
    }

    @DeleteMapping("/delete/{idClient}")
    public Client Delete(@PathVariable Integer idClient) {
        Client client = new Client();
        client.setIdClient(idClient);
        return clientServiceImpl.delete(client);
    }

    @PostMapping("/find_by_id")
    public Client findById(@RequestBody Client client) {
        return clientServiceImpl.findById(client.getIdClient());
    }

    @GetMapping("/find_all")
    public List<Client> findAll() {
        return clientServiceImpl.findAll();
    }

    @GetMapping("/find_all_command_by_id_client/{idClient}")
    public List<Commande> findAllCommande(@PathVariable Integer idClient) {
        return clientServiceImpl.findAllCommandeByClient(idClient);
    }
}

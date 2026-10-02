package com.ucao.dgi.l3.controller;

import com.ucao.dgi.l3.entity.Client;
import com.ucao.dgi.l3.entity.Commande;
import com.ucao.dgi.l3.service.ClientService;
import com.ucao.dgi.l3.service.CommandeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final CommandeService commandeService;

    @GetMapping
    public List<Client> findAll() {
        return clientService.findAll();
    }

    @GetMapping("/{id}")
    public Client findById(@PathVariable Integer id) {
        return clientService.findById(id);
    }

    @GetMapping("/{id}/commandes")
    public List<Commande> commandes(@PathVariable Integer id) {
        clientService.findById(id);
        return commandeService.findAllByClient(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Client save(@Valid @RequestBody Client client) {
        return clientService.save(client);
    }

    @PutMapping("/{id}")
    public Client update(@PathVariable Integer id, @Valid @RequestBody Client client) {
        return clientService.update(id, client);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        clientService.delete(id);
    }
}

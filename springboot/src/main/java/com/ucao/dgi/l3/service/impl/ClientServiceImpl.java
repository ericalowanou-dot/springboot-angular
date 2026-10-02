package com.ucao.dgi.l3.service.impl;

import com.ucao.dgi.l3.entity.Client;
import com.ucao.dgi.l3.exception.RegleMetierException;
import com.ucao.dgi.l3.exception.RessourceIntrouvableException;
import com.ucao.dgi.l3.repository.ClientRepository;
import com.ucao.dgi.l3.repository.CommandeRepository;
import com.ucao.dgi.l3.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final CommandeRepository commandeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Client> findAll() {
        return clientRepository.findAll(Sort.by("nom", "prenom"));
    }

    @Override
    @Transactional(readOnly = true)
    public Client findById(Integer id) {
        return clientRepository.findById(id).orElseThrow(() -> new RessourceIntrouvableException("Client", id));
    }

    @Override
    public Client save(Client client) {
        client.setIdClient(null);
        return clientRepository.saveAndFlush(client);
    }

    @Override
    public Client update(Integer id, Client donnees) {
        Client client = findById(id);
        client.setNom(donnees.getNom());
        client.setPrenom(donnees.getPrenom());
        client.setAddress(donnees.getAddress());
        client.setTelephone(donnees.getTelephone());
        client.setEmail(donnees.getEmail());
        clientRepository.flush(); // détecte tout de suite un doublon (nom, prénom, téléphone)
        return client;
    }

    @Override
    public void delete(Integer id) {
        Client client = findById(id);
        long nb = commandeRepository.countByClientIdClient(id);
        if (nb > 0) {
            throw new RegleMetierException("Ce client a " + nb + " commande(s) : il ne peut pas être supprimé");
        }
        clientRepository.delete(client);
    }
}

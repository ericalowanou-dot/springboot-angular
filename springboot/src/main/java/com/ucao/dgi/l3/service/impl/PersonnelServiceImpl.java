package com.ucao.dgi.l3.service.impl;

import com.ucao.dgi.l3.dto.PersonnelDTO;
import com.ucao.dgi.l3.entity.MembrePersonnel;
import com.ucao.dgi.l3.entity.Personnel;
import com.ucao.dgi.l3.repository.PersonnelRepository;
import com.ucao.dgi.l3.service.PersonnelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonnelServiceImpl implements PersonnelService {

    @Autowired
    private PersonnelRepository personnelRepository;

    @Override
    public List<Personnel> findAllPersonnel() {
        return personnelRepository.findAll();
    }

    @Override
    public Personnel findPersonnelById(Integer idPersonnel) {
        return personnelRepository.findById(idPersonnel).orElse(null);
    }

    @Override
    public Personnel savePersonnel(PersonnelDTO personnelDTO) {
        MembrePersonnel personnel = new MembrePersonnel();
        personnel.setNom(personnelDTO.getNom());
        personnel.setPrenom(personnelDTO.getPrenom());
        personnel.setFonction(personnelDTO.getFonction());
        return personnelRepository.save(personnel);
    }

    @Override
    public Personnel updatePersonnel(PersonnelDTO personnelDTO) {
        Personnel personnel = personnelRepository.findById(personnelDTO.getIdPersonnel())
                .orElse(new MembrePersonnel());
        
        personnel.setNom(personnelDTO.getNom());
        personnel.setPrenom(personnelDTO.getPrenom());
        personnel.setFonction(personnelDTO.getFonction());
        
        return personnelRepository.save(personnel);
    }

    @Override
    public Personnel deletePersonnel(Integer idPersonnel) {
        Personnel p = personnelRepository.findById(idPersonnel).orElse(null);
        if (p != null) {
            personnelRepository.delete(p);
        }
        return p;
    }

    @Override
    public Personnel findPersonnelServeur() {
        return personnelRepository.findByFonction("Serveur");
    }

    @Override
    public Personnel findPersonnelLivreur() {
        return personnelRepository.findByFonction("Livreur");
    }

    @Override
    public Personnel findPersonnelChef() {
        return personnelRepository.findByFonction("Chef");
    }
}

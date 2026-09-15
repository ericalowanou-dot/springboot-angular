package com.ucao.dgi.l3.service;

import com.ucao.dgi.l3.dto.PersonnelDTO;
import com.ucao.dgi.l3.entity.Personnel;

import java.util.List;

public interface PersonnelService {
    public List<Personnel> findAllPersonnel();

    public Personnel findPersonnelById(Integer idPersonnel);

    public Personnel savePersonnel(PersonnelDTO personnelDTO);

    public Personnel updatePersonnel(PersonnelDTO personnelDTO);

    public Personnel deletePersonnel(Integer idPersonnel);

    public Personnel findPersonnelServeur();
    public Personnel findPersonnelLivreur();
    public Personnel findPersonnelChef();

}
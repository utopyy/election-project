package com.ipamc.election.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipamc.election.data.entity.Categorie;
import com.ipamc.election.data.entity.Proposition;
import com.ipamc.election.repository.CategorieRepository;
import com.ipamc.election.repository.PropositionRepository;

@Service
@Transactional 
public class CategorieService {

	@Autowired
    private CategorieRepository categorieRepository;

    public CategorieService() {

        
    }
    
    public Categorie findById(Long id) {
    	return categorieRepository.findById(id);
    }
}

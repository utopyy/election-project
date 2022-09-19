package com.ipamc.election.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipamc.election.data.entity.Proposition;
import com.ipamc.election.repository.PropositionRepository;


@Service
@Transactional 
public class PropositionService {

	@Autowired
    private PropositionRepository propositionRepository;

    public PropositionService() {

        
    }
    
    public boolean existsByLibelle(String libelle) {
    	return propositionRepository.existsByLibelle(libelle);
    }
    public Proposition findByLibelle(String libelle) {
    	return propositionRepository.findByLibelle(libelle);
    }
}


package com.ipamc.election.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipamc.election.data.entity.Proposition;
import com.ipamc.election.data.entity.User;

public interface PropositionRepository extends JpaRepository<Proposition,Integer> {

	Proposition findByLibelle(String libelle);
	
	Boolean existsByLibelle(String libelle);
	
}

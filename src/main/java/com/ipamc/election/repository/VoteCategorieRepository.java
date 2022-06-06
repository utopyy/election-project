package com.ipamc.election.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipamc.election.data.entity.VoteCategorie;
import com.ipamc.election.data.entity.VoteCategorieId;

public interface VoteCategorieRepository extends JpaRepository<VoteCategorie, VoteCategorieId> {
	
}


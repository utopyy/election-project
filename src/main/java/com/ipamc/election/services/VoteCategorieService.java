package com.ipamc.election.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipamc.election.data.entity.VoteCategorie;
import com.ipamc.election.repository.VoteCategorieRepository;

@Service
@Transactional 
public class VoteCategorieService {
	
	@Autowired
	VoteCategorieRepository voteCategorieRepo;
	
	public VoteCategorieService() {
		
	}
	
	public void saveVoteCategorie(VoteCategorie voteCategorie) {
		voteCategorieRepo.save(voteCategorie);
	}
}

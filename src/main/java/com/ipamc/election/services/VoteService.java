package com.ipamc.election.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.ipamc.election.data.entity.Jure;
import com.ipamc.election.data.entity.Question;
import com.ipamc.election.data.entity.User;
import com.ipamc.election.data.entity.Vote;
import com.ipamc.election.data.entity.VoteCategorie;
import com.ipamc.election.repository.VoteCategorieRepository;
import com.ipamc.election.repository.VoteRepository;

@Service
@Transactional 
public class VoteService {

	@Autowired
	VoteRepository voteRepository;
	@Autowired 
	VoteCategorieRepository voteCategorie;
	
	public VoteService() {
	
	}
	
	public Vote getVoteByJureAndQuestion(Jure jure, Question quest) {
		return voteRepository.findByJureAndQuestion(jure, quest);
	}
	
	public void saveVote(Vote vote) {
		voteRepository.save(vote);
	}
	
	public void updateVote(Vote vote) {
		voteRepository.save(vote);
	}
}
	
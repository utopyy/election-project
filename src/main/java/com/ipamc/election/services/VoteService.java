package com.ipamc.election.services;

import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.ipamc.election.data.entity.Jure;
import com.ipamc.election.data.entity.Proposition;
import com.ipamc.election.data.entity.Question;
import com.ipamc.election.data.entity.Session;
import com.ipamc.election.data.entity.User;
import com.ipamc.election.data.entity.Vote;
import com.ipamc.election.data.entity.VoteCategorie;
import com.ipamc.election.repository.JureRepository;
import com.ipamc.election.repository.QuestionRepository;
import com.ipamc.election.repository.VoteCategorieRepository;
import com.ipamc.election.repository.VoteRepository;

@Service
@Transactional 
public class VoteService {

	@Autowired
	VoteRepository voteRepository;
	@Autowired 
	VoteCategorieRepository voteCategorieRepository;
	@Autowired 
	JureRepository jureRepository;
	@Autowired
	QuestionRepository questionRepository;

	public VoteService() {

	}

	public Vote getVoteByJureAndQuestion(Jure jure, Question quest) {
		return voteRepository.findByJureAndQuestion(jure, quest);
	}

	public void saveVote(Vote vote, Set<VoteCategorie> votesCategories, Session activeSession, User authenticatedUser) {
		vote.setJure(jureRepository.findBySessionAndUser(activeSession, authenticatedUser));
		Vote voteCheck =  voteRepository.findByJureAndQuestion(vote.getJure(), vote.getQuestion());
		if(voteCheck==null) {
				voteRepository.save(vote);
				for(VoteCategorie voteCat : votesCategories) {
					voteCategorieRepository.save(voteCat);
				}
		}
	}

	public void updateVote(Vote vote) {
		voteRepository.save(vote);
	}
	
	public void removeVotesChildFromQuestion(Question question) {
		Question quest = questionRepository.findById(question.getId());
		for(Vote vote : quest.getVotes()) {
			for(Proposition prop : vote.getPropositions()) {
				vote.removeProposition(prop);
			}
			for(VoteCategorie vc : vote.getVotesCategories()) {
				vc.setVote(null);
			}
			vote.getVotesCategories().clear();
			voteRepository.save(vote);
		}

	}
	
	public void removeVotesFromQuestion(Question question) {
		Question quest = questionRepository.findById(question.getId());
		for(Vote vote : quest.getVotes()) {
			voteRepository.deleteVote(vote.getId());
		}
	}
}

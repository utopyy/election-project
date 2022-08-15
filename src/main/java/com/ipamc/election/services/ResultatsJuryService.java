package com.ipamc.election.services;

import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipamc.election.data.entity.Question;
import com.ipamc.election.data.entity.ResultatsJury;
import com.ipamc.election.data.entity.Vote;
import com.ipamc.election.repository.QuestionRepository;
import com.ipamc.election.repository.ResultatsJuryRepository;
import com.ipamc.election.repository.VoteRepository;

@Service
@Transactional
public class ResultatsJuryService {
	
	@Autowired
	ResultatsJuryRepository resultsRepo;
	@Autowired
	VoteRepository voteRepository;
	@Autowired
	QuestionRepository questRepository;
	
	public ResultatsJury getLastResults() {
		return resultsRepo.findFirstByOrderByDateDesc();
	}
	
	public void createResultats(Question question) {
		Set<Vote> votes = voteRepository.findAllByQuestion(question);
		ResultatsJury results = new ResultatsJury();
		resultsRepo.save(results);
		System.out.println("Votes size: "+votes.size());
		for(Vote vote : votes) {
			results.getVotes().add(vote);
			vote.setResultats(results);
			System.out.println("vote detail: "+vote.getResultats().getId());
			voteRepository.save(vote);
		}
		question.setIsActive(false);
		questRepository.save(question);
	}

}

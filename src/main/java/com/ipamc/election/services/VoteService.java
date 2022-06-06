package com.ipamc.election.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipamc.election.data.entity.User;
import com.ipamc.election.data.entity.Vote;
import com.ipamc.election.repository.VoteRepository;

@Service
@Transactional 
public class VoteService {

	@Autowired
	VoteRepository voteRepository;
	
	public VoteService() {
	
	}
}
	
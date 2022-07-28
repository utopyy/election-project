package com.ipamc.election.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipamc.election.data.entity.Jure;
import com.ipamc.election.data.entity.Session;
import com.ipamc.election.data.entity.User;
import com.ipamc.election.repository.JureRepository;

@Service
@Transactional 
public class JureService {
	
	@Autowired JureRepository jureRepository;
	
	public JureService() {
		
	}
	
	public Jure findBySessionAndUser(Session session, User user) {
		return jureRepository.findBySessionAndUser(session, user);
	}

}

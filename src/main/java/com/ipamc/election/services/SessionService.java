package com.ipamc.election.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipamc.election.data.entity.Session;
import com.ipamc.election.data.entity.User;
import com.ipamc.election.repository.SessionRepository;

@Service
@Transactional 
public class SessionService {

	@Autowired
	SessionRepository sessionRepository;
	
	public SessionService() {
		
	}
	
	public Boolean checkSessionAccess(User user) {
		if(sessionRepository.existsByIsActive(true)) {
			Session session = sessionRepository.findByIsActive(true);
			return session.userAllowed(user);
		}else {
			return false;
		}
	}
	
	public Session getActiveSession() {
		return sessionRepository.findByIsActive(true);
	}
}

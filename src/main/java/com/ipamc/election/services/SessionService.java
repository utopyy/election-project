package com.ipamc.election.services;

import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipamc.election.data.entity.Question;
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
	
	public void enableVoteQuestion(Long id) {
		Session session = sessionRepository.findByIsActive(true);
		for(Question question : session.getQuestions()) {
			if(question.getId().equals(id)) {
				question.setVoteEnabled(true);
				break;
			}
		}
	}
	
	public Session createSession(String name, Set<User> users, Set<Question> questions) {
		Session session = new Session(name, users);
		session.addQuestions(questions);
		System.out.println("Sessiondi: "+session.getId());
		System.out.println("Session name: "+session.getName());
		return sessionRepository.save(session);
	}
}

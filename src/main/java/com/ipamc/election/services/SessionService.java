package com.ipamc.election.services;

import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipamc.election.data.entity.Question;
import com.ipamc.election.data.entity.Session;
import com.ipamc.election.data.entity.User;
import com.ipamc.election.repository.QuestionRepository;
import com.ipamc.election.repository.SessionRepository;

@Service
@Transactional 
public class SessionService {

	@Autowired
	SessionRepository sessionRepository;
	@Autowired
	QuestionRepository questRepository;
	
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
	
	public void enableVoteQuestion() {
		Question question = questRepository.findByIsActive(true);
		question.setVoteEnabled(true);
		questRepository.save(question);
	}
	
	
	
	public Session createSession(String name, Set<User> users) {
		Session session = new Session(name, users);
		return sessionRepository.save(session);
	}
	
	public void addQuestion(Session session, Question question) {
		Session sessDb = sessionRepository.getById(session.getId());
		sessDb.addQuestion(question);
		sessionRepository.save(sessDb);
	}
	
	
}

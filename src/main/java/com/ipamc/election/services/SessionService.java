package com.ipamc.election.services;

import java.util.List;
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
	
	public List<Session> findAllSessions(){
		return sessionRepository.findAll();
	}
	
	public List<Session> findSessionsNotArchived(){
		return sessionRepository.findAllByArchived(false);
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
	
	public Long getNumberOfSessions() {
		return Long.valueOf(sessionRepository.findAllByArchived(false).size());
	}
	
	public void removeSession(Long id) {
		//Check si isActive;
		Session sess = sessionRepository.getById(id);
		sessionRepository.delete(sess);
	}
	
	public Session getBySessionName(String name) {
		return sessionRepository.findByName(name);
	}
	
	public void save(Session session) {
		sessionRepository.save(session);
	}
	
	public void updateActiveSession(Session session) {
		if(!session.getIsActive()) {
			if(sessionRepository.findByIsActive(true)!=null) {
				Session oldActive = sessionRepository.findByIsActive(true);
				oldActive.setIsActive(false);
				sessionRepository.save(oldActive);
			}
			session.setIsActive(true);
			sessionRepository.save(session);
		}
	}
	
	public void archive(Session session) {
		Session toArchive = sessionRepository.findByName(session.getName());
		toArchive.setIsActive(false);
		toArchive.setArchived(true);
		sessionRepository.save(toArchive);
	}
	
	public void pauseSession(Session session) {
		session.setIsActive(false);
		sessionRepository.save(session);
	}
	
	
}

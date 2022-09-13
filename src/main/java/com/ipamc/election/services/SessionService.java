package com.ipamc.election.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipamc.election.data.entity.Jure;
import com.ipamc.election.data.entity.Question;
import com.ipamc.election.data.entity.Session;
import com.ipamc.election.data.entity.User;
import com.ipamc.election.repository.JureRepository;
import com.ipamc.election.repository.QuestionRepository;
import com.ipamc.election.repository.SessionRepository;
import com.ipamc.election.repository.UserRepository;

@Service
@Transactional 
public class SessionService {

	@Autowired
	SessionRepository sessionRepository;
	@Autowired
	QuestionRepository questRepository;
	@Autowired
	JureRepository jureRepository;
	@Autowired
	UserRepository userRepository;

	public SessionService() {

	}

	public Boolean checkSessionAccess(User user) {
		if(sessionRepository.existsByIsActive(true)) {
			Session session = sessionRepository.findByIsActive(true);
			for(Jure jure : user.getJures()) {
				if(session.jureAllowed(jure)) {
					return true;
				}
			}
			return false;
		}
		return false;
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

	public Session createSession(String name, Set<User> users) {
		Session sess = new Session(name);
		sessionRepository.save(sess);
		for(User user : users) {
			Jure jure = new Jure(sess, user);
			jureRepository.save(jure);
		} 
		return sess;
	}


	public void addQuestion(Session session, Question question) {
		Session sessDb = sessionRepository.getById(session.getId());
		sessDb.addQuestion(question);
		sessionRepository.save(sessDb);
	}
	
	public void removeQuestion(Session session, Question question) {
		Session sessDb = sessionRepository.getById(session.getId());
		sessDb.removeQuestion(question);
		sessionRepository.save(session);
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
		if(sessionRepository.findByIsActive(true)!=null) {
			Session oldActive = sessionRepository.findByIsActive(true);
			oldActive.setIsActive(false);
			sessionRepository.save(oldActive);
		}
		session.setIsActive(true);
		sessionRepository.save(session);
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

	public Boolean jureHasJoined(User user) {
		try{ 
			Session session = sessionRepository.findByIsActive(true);
			for(Jure jure : session.getJures()) {
				if(jure.getUser().equals(user)) {
					return jure.getHasJoined();
				}
			}
		}catch(NullPointerException e) {
			System.err.print(e);
			return false;
		}
		return false;
	}
	
	public void removeActiveQuestion(Session session) {
		try{ 
			Question quest = session.getActiveQuestion();
			quest.setIsActive(false);
			questRepository.save(quest);
		}catch(NullPointerException ex) {}
	}
	
	public Session updateSession(Session session, Set<User> users, List<Question> questions, QuestionService questService) {
		Session sessToUpdate = sessionRepository.getById(session.getId());
		sessToUpdate.setName(session.getName());
		Set<Jure> jury = new HashSet<>();
		for(User user : users) {
			Jure jure;
			if(jureRepository.existsBySessionAndUser(session, user)){
				jure = jureRepository.findBySessionAndUser(session, user);
				jure.setArchived(false);
			}else {
				jure = new Jure(sessToUpdate, user);
			}
			jureRepository.save(jure);
			jury.add(jure);
		}
		sessToUpdate.setJures(jury);
		for(Jure jure : session.getJures()) {
			if(!jury.contains(jure)) {
				if(jure.getVotes().size() > 0) {
					jure.setArchived(true);
					jureRepository.save(jure);
				}else {
					jureRepository.delete(jure);
				}
			}
		}	
		Set<Question> quests = new HashSet<>();
		for(Question question : questions) {
			Question quest;
			if(questRepository.existsByIntituleAndSession(question.getIntitule(),session)) {
				if(question.getId() != null) {
					quest = question;
				}else {
					System.out.println("ici?");
					quest = questService.createQuestion(question, session);
				}
			}else {
				System.out.println("ici?loooo");
				quest = questService.createQuestion(question, session);
			}
			quests.add(quest);
		}
		System.out.println("en fait le soucis est lla");
		for(Question question : session.getQuestions()) {
			System.out.println("en fait le soucis est oola");
			if(!quests.contains(question)) {
				System.out.println("en fait le sopoucis est la");
				questRepository.deleteById(question.getId());
			}
		}
		System.out.println("en fait le soucis est la");
		System.out.println("sess : "+sessToUpdate.getId());
		System.out.println("sess : "+sessToUpdate.getName());
		for(Question quest : sessToUpdate.getQuestions()) {
			System.out.println("quest : "+quest.getId()+ ", "+quest.getIntitule());
		}
		return sessionRepository.save(sessToUpdate);
	}
	
	public List<Session> loadArchivedSessions(){
		return sessionRepository.findAllByArchived(true);
	}
	
	public void lockSession(Session sess, Boolean lock) {
		sess.setIsLocked(lock);
		sessionRepository.save(sess);
	}

}

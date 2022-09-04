package com.ipamc.election.services;

import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipamc.election.data.entity.Categorie;
import com.ipamc.election.data.entity.Proposition;
import com.ipamc.election.data.entity.Question;
import com.ipamc.election.data.entity.Session;
import com.ipamc.election.data.entity.Vote;
import com.ipamc.election.repository.CategorieRepository;
import com.ipamc.election.repository.PropositionRepository;
import com.ipamc.election.repository.QuestionRepository;
import com.ipamc.election.repository.SessionRepository;

@Service
@Transactional 
public class QuestionService {

	@Autowired
	QuestionRepository questRepository;
	@Autowired
	CategorieRepository catRepository;
	@Autowired
	PropositionRepository propRepository;

	public QuestionService() {

	}

	@Transactional
	public Question createQuestion(Question question, Session session) {
		Question quest = new Question(question.getIntitule(), question.getMultiChoice(), question.getPropositionRequired());
		quest.setSession(session);
		for(Categorie cat : question.getCategories()) {
			if(catRepository.existsByLibelleAndValeurAndIsRequired(cat.getLibelle(), cat.getValeur(), cat.getIsRequired())) {
				quest.addCategorie(catRepository.findByLibelleAndValeurAndIsRequired(cat.getLibelle(), cat.getValeur(), cat.getIsRequired()));
			}else {
				Categorie newCat = new Categorie(cat.getLibelle(), cat.getValeur(), cat.getIsRequired());
				catRepository.save(newCat);
				quest.addCategorie(newCat);
			}
		}
		for(Proposition p : question.getPropositions()) {
			if(propRepository.existsByLibelle(p.getLibelle())) {
				quest.addProposition(propRepository.findByLibelle(p.getLibelle()));
			}else {
				Proposition newProp = new Proposition(p.getLibelle());
				propRepository.save(newProp);
				quest.addProposition(propRepository.findByLibelle(p.getLibelle()));
			}
		}
		return questRepository.save(quest);
	}

	public void activeQuestion(Session activeSession, Question activeQuestion) {
		Question oldActiveQuestion = null;
		for(Question quest : activeSession.getQuestions()) {
			if(quest.getIsActive()) {
				oldActiveQuestion = quest;
			}
		}
		if(oldActiveQuestion!=null) {
			questRepository.activateQuestion(false, oldActiveQuestion.getId());
		}
		questRepository.activateQuestion(true, activeQuestion.getId());
	}
	
	public Question getById(Long id) {
		return questRepository.findById(id);
	}

	public Question updateQuestion(Long id, Question question) {
		Question quest = questRepository.findById(id);
		System.out.println("Question to update: "+quest.getIntitule());
		quest.getCategories().clear();
		quest.getPropositions().clear();
		quest.setIntitule(question.getIntitule());
		quest.setMultiChoice(question.getMultiChoice());
		quest.setPropositionRequired(question.getPropositionRequired());
		for(Categorie cat : question.getCategories()) {
			if(catRepository.existsByLibelleAndValeurAndIsRequired(cat.getLibelle(), cat.getValeur(), cat.getIsRequired())) {
				quest.addCategorie(catRepository.findByLibelleAndValeurAndIsRequired(cat.getLibelle(), cat.getValeur(), cat.getIsRequired()));
			}else {
				Categorie newCat = new Categorie(cat.getLibelle(), cat.getValeur(), cat.getIsRequired());
				catRepository.save(newCat);
				quest.addCategorie(newCat);
			}
		}for(Proposition p : question.getPropositions()) {
			if(propRepository.existsByLibelle(p.getLibelle())) {
				quest.addProposition(propRepository.findByLibelle(p.getLibelle()));
			}else {
				Proposition newProp = new Proposition(p.getLibelle());
				propRepository.save(newProp);
				quest.addProposition(propRepository.findByLibelle(p.getLibelle()));
			}
		}
		return questRepository.save(quest);

	}
	
	public void removeQuestion(Question question) {
		questRepository.delete(question);
	}
	
	public Question getLastResults() {
		Question quest = questRepository.findFirstByOrderByDateVotesDesc();
		if(quest.getDateVotes() == null)
			return null;
		return quest;
	}
	
	public void createResultats(Question question) {
		question.addVoteTime();
		questRepository.save(question);
	}



}

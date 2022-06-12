package com.ipamc.election.services;

import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipamc.election.data.entity.Categorie;
import com.ipamc.election.data.entity.Proposition;
import com.ipamc.election.data.entity.Question;
import com.ipamc.election.data.entity.Session;
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
	public Question createQuestion(Set<Categorie> cats, Set<Proposition> props, String intitule, Boolean mutliChoice, Session session) {
		Question quest = new Question(intitule, mutliChoice);
		quest.setSession(session);
		for(Categorie cat : cats) {
			if(catRepository.existsByLibelleAndValeurAndIsRequired(cat.getLibelle(), cat.getValeur(), cat.getIsRequired())) {
				quest.addCategorie(catRepository.findByLibelleAndValeurAndIsRequired(cat.getLibelle(), cat.getValeur(), cat.getIsRequired()));
			}else {
				Categorie newCat = new Categorie(cat.getLibelle(), cat.getValeur(), cat.getIsRequired());
				catRepository.save(newCat);
				quest.addCategorie(newCat);
			}
		}for(Proposition p : props) {
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
}

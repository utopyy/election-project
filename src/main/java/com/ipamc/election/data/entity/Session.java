package com.ipamc.election.data.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.sun.istack.NotNull;

@Entity
@Table(name="Sessions")
public class Session {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;	
	@NotNull
	private String name;
    @OneToMany(mappedBy="session", fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<Jure> jures = new HashSet<>();
	private Boolean isActive;
	@OneToMany(mappedBy = "session", fetch = FetchType.EAGER, orphanRemoval = true)
	private Set<Question> questions = new HashSet<Question>(); 
	private Boolean archived;


	public Session() {
		this.archived = false;
		this.isActive = false;
	}

	public Session(String name) {
		this.name = name;
		isActive = false;
		archived = false;
	}
	public Session(String name, Set<Jure> jures) {
		super();
		this.name = name;
		this.jures = jures;
		isActive = false;
		archived = false;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	
	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Set<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(Set<Question> questions) {
		this.questions = questions;
	}


	public void addQuestion(Question question) {
		question.setSession(this);
		this.questions.add(question);
	}
	
	public void removeQuestion(Question question) {
		question.setSession(null);
		this.questions.remove(question);
	}

	public void removeQuestions() {
		questions.clear();
	}

	public Set<Jure> getJures() {
		return jures;
	}

	public void setJures(Set<Jure> jures) {
		this.jures.clear();
		if(jures!=null) {
			this.jures.addAll(jures);
		}
	}

	public Boolean getArchived() {
		return archived;
	}

	public void setArchived(Boolean archived) {
		this.archived = archived;
	}
	
	public Boolean jureAllowed(Jure jure) {
		for(Jure j : jures) {
			if(j.equals(jure))
				return true;
		}
		return false;
	}
	
	public Question getQuestion(Question question) {
		for(Question quest : questions) {
			if(quest.getIntitule().equals(question.getIntitule())) {
				return quest;
			}
		}
		return null;
	}
	
	public Question getActiveQuestion() {
		for(Question quest : questions) {
			if(quest.getIsActive())
				return quest;
		}
		return null;
	}
	
	public List<Question> getUnansweredQuestions(){
		List<Question> questions = new ArrayList<>();
		for(Question question : this.questions) {
			if(question.getVotes().size()>0 && question.getDateVotes() != null) {

			}else {
				questions.add(question);
			}
		}
		return questions;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Session other = (Session) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
}

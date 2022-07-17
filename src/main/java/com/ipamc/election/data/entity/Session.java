package com.ipamc.election.data.entity;

import java.util.HashSet;
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
	@OneToMany(mappedBy = "session", cascade= {CascadeType.ALL, CascadeType.REMOVE}, fetch = FetchType.EAGER)
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
		this.jures = jures;
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
	
	public Question getActiveQuestion() {
		for(Question quest : questions) {
			if(quest.getIsActive())
				return quest;
		}
		return null;
	}
	
	
	
}

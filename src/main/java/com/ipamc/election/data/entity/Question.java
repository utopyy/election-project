package com.ipamc.election.data.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;


import org.springframework.transaction.annotation.Transactional;


@Entity
@Table(name="Questions")

public class Question {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String intitule;
	@ManyToOne
	@JoinColumn(name = "idSession", referencedColumnName = "id")
	private Session session;
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	@JoinTable(	name = "Questions_categories", joinColumns = @JoinColumn(name = "id_question"), 
				inverseJoinColumns = @JoinColumn(name = "id_categorie"))
	private Set<Categorie> categories = new HashSet<>();
	@ManyToMany(cascade=CascadeType.MERGE, fetch = FetchType.EAGER)  
	@JoinTable(	name = "Questions_propositions", joinColumns = @JoinColumn(name = "id_question"), 
				inverseJoinColumns = @JoinColumn(name = "id_proposition"))
	private Set<Proposition> propositions = new HashSet<>();
	@OneToMany(mappedBy="question", fetch = FetchType.EAGER, cascade=CascadeType.ALL)
	private Set<Vote> votes = new HashSet<>();
	
    private Boolean propositionRequired;
	private Boolean multiChoice;
	private Boolean isActive;
	private Boolean voteEnabled;
	
	
	public Question(String intitule, Boolean multiChoice, Boolean propositionRequired) {
		this.intitule = intitule;
		this.propositionRequired = propositionRequired;
		this.multiChoice = multiChoice;
		isActive = false;
		voteEnabled = false;
	}
	
	public Question(String intitule, Set<Proposition> propositions, Set<Categorie> categories, Boolean multiChoice) {
		this.intitule = intitule;
		this.propositions = propositions;
		this.categories = categories;
		this.multiChoice = multiChoice;
		isActive = false;
		voteEnabled = false;	
	}
	
	public Question() {
		isActive = false;
		voteEnabled = false;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIntitule() {
		return intitule;
	}

	public void setIntitule(String intitule) {
		this.intitule = intitule;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public Set<Categorie> getCategories() {
		return categories;
	}

	
	public void setCategories(Set<Categorie> categories) {
		this.categories = categories;
	}

	public Set<Proposition> getPropositions() {
		return propositions;
	}

	public void setPropositions(Set<Proposition> propositions) {
		this.propositions = propositions;
	}

	public Boolean getMultiChoice() {
		return multiChoice;
	}

	public void setMultiChoice(Boolean multiChoice) {
		this.multiChoice = multiChoice;
	}

	public Set<Vote> getVotes() {
		return votes;
	}

	public void setVotes(Set<Vote> votes) {
		this.votes = votes;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Boolean getVoteEnabled() {
		return voteEnabled;
	}

	public void setVoteEnabled(Boolean voteEnabled) {
		this.voteEnabled = voteEnabled;
	}
	
	public void addCategorie(Categorie cat) {
		cat.addQuestion(this);
		categories.add(cat);
	}
	
	public void addProposition(Proposition prop) {
		prop.addQuestion(this);
		propositions.add(prop);
	}

	public Boolean getPropositionRequired() {
		return propositionRequired;
	}

	public void setPropositionRequired(Boolean propositionRequired) {
		this.propositionRequired = propositionRequired;
	}
	
	public void removeProposition(Proposition proposition) {
		propositions.remove(proposition);
	}
	
	public void removeCategorie(Categorie categorie) {
		categories.remove(categorie);
	}
	
	
}

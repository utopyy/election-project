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
import javax.persistence.ManyToOne;
import javax.persistence.Table;


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
	@ManyToMany(cascade=CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(	name = "Questions_categories", joinColumns = @JoinColumn(name = "id_question"), 
				inverseJoinColumns = @JoinColumn(name = "id_categorie"))
	private Set<Categorie> categories = new HashSet<>();
	@ManyToMany(cascade=CascadeType.ALL, fetch = FetchType.EAGER)  
	@JoinTable(	name = "Questions_propositions", joinColumns = @JoinColumn(name = "id_question"), 
				inverseJoinColumns = @JoinColumn(name = "id_proposition"))
	private Set<Proposition> propositions = new HashSet<>();
	
	public Question(String intitule) {
		this.intitule = intitule;
	}
	
	public Question() {
		
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
}

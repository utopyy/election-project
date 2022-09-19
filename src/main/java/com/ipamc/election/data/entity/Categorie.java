package com.ipamc.election.data.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="categories", schema = "dbo")

public class Categorie {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String libelle;
	private Integer valeur = -1;
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "categories")
	Set<Question> questions = new HashSet<>();;
	/*@ManyToMany(mappedBy = "categories")
	Set<Vote> votes;**/
	private Boolean isRequired;
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, mappedBy = "categorie")
	private Set<VoteCategorie> votesCategories = new HashSet<>();
	
	
	public Categorie() {
		isRequired = false;
	}
	
	public Categorie(String libelle, Integer valeur, Boolean isRequired) {
		this.libelle = libelle;
		if(valeur != null) {
			this.valeur = valeur;
		}
		this.isRequired = isRequired;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(Set<Question> questions) {
		this.questions = questions;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public Integer getValeur() {
		return valeur;
	}

	public void setValeur(Integer valeur) {
		this.valeur = valeur;
	}

	public Boolean getIsRequired() {
		return isRequired;
	}

	public void setIsRequired(Boolean isRequired) {
		this.isRequired = isRequired;
	}

	/*public Set<Vote> getVotes() {
		return votes;
	}

	public void setVotes(Set<Vote> votes) {
		this.votes = votes;
	}	**/


	public void addQuestion(Question quest) {
		questions.add(quest);
	}
	
	public Set<VoteCategorie> getVotesCategories() {
		return votesCategories;
	}
	
	public Boolean isNoteCategory() {
		return libelle.equals("Note");
	}
	
	public Boolean isComCategory() {
		return libelle.equals("Commentaire");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((isRequired == null) ? 0 : isRequired.hashCode());
		result = prime * result + ((libelle == null) ? 0 : libelle.hashCode());
		result = prime * result + ((questions == null) ? 0 : questions.hashCode());
		result = prime * result + ((valeur == null) ? 0 : valeur.hashCode());
		result = prime * result + ((votesCategories == null) ? 0 : votesCategories.hashCode());
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
		Categorie other = (Categorie) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isRequired == null) {
			if (other.isRequired != null)
				return false;
		} else if (!isRequired.equals(other.isRequired))
			return false;
		if (libelle == null) {
			if (other.libelle != null)
				return false;
		} else if (!libelle.equals(other.libelle))
			return false;
		if (questions == null) {
			if (other.questions != null)
				return false;
		} else if (!questions.equals(other.questions))
			return false;
		if (valeur == null) {
			if (other.valeur != null)
				return false;
		} else if (!valeur.equals(other.valeur))
			return false;
		if (votesCategories == null) {
			if (other.votesCategories != null)
				return false;
		} else if (!votesCategories.equals(other.votesCategories))
			return false;
		return true;
	}

	public void setVotesCategories(Set<VoteCategorie> votesCategories) {
		this.votesCategories = votesCategories;
	}
	
	public void addVotesCategories(VoteCategorie votesCategories) {
		this.votesCategories.add(votesCategories);
	}
    
}

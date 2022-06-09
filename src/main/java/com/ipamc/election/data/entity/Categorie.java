package com.ipamc.election.data.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalIdCache;

@Entity
@Table(name="Categories")

public class Categorie {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String libelle;
	private int valeur;
	@ManyToMany(mappedBy = "categories")
	Set<Question> questions;
	/*@ManyToMany(mappedBy = "categories")
	Set<Vote> votes;**/
	private Boolean isRequired;
	@OneToMany(mappedBy = "categorie")
	private Set<VoteCategorie> votesCategories = new HashSet<>();
	
	
	public Categorie() {
		isRequired = false;
	}
	
	public Categorie(String libelle, int valeur, Boolean isRequired) {
		this.libelle = libelle;
		this.valeur = valeur;
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

	public int getValeur() {
		return valeur;
	}

	public void setValeur(int valeur) {
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

	
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Categorie tag = (Categorie) o;
        return Objects.equals(id, tag.id);
    }
 
 

	public Set<VoteCategorie> getVotesCategories() {
		return votesCategories;
	}

	public void setVotesCategories(Set<VoteCategorie> votesCategories) {
		this.votesCategories = votesCategories;
	}
	
	public void addVotesCategories(VoteCategorie votesCategories) {
		this.votesCategories.add(votesCategories);
	}

	@Override
    public int hashCode() {
        return Objects.hash(id);
    }
	
    
}

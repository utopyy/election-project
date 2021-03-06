package com.ipamc.election.data.entity;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalIdCache;

import com.sun.istack.NotNull;

@Entity
@Table(name="Votes")
@NaturalIdCache
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Vote {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;	
	@NotNull
    private LocalDateTime date;
	@ManyToOne
	@JoinColumn(name = "idUtilisateur", referencedColumnName = "id")
	private User user;
	@ManyToOne
	@JoinColumn(name = "idQuestion", referencedColumnName = "id")
	private Question question;
	/*@ManyToMany(cascade=CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(	name = "Votes_categories", joinColumns = @JoinColumn(name = "id_vote"), 
				inverseJoinColumns = @JoinColumn(name = "id_categorie"))
	private Set<Categorie> categories = new HashSet<>();**/

	@OneToMany(mappedBy = "vote", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<VoteCategorie> votesCategories = new HashSet<>();

	@ManyToMany(cascade=CascadeType.MERGE, fetch = FetchType.EAGER)  
	@JoinTable(	name = "Votes_propositions", joinColumns = @JoinColumn(name = "id_vote"), 
				inverseJoinColumns = @JoinColumn(name = "id_proposition"))
	private Set<Proposition> propositions = new HashSet<>();
	
	
	public Vote() {}
	
	public Vote(User user, Question question, Set<Proposition> propositions) {
		date = (OffsetDateTime.now( ZoneOffset.UTC )).toLocalDateTime() ;
		this.question = question;
		this.propositions = propositions;
		this.user = user;
		//this.categories = categories;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public LocalDateTime getDate() {
		return date;
	}
	public void setDate(LocalDateTime date) {
		this.date = date;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}	
	/*public Set<Categorie> getCategories() {
		return categories;
	}
	public void setCategories(Set<Categorie> categories) {
		this.categories = categories;
	}**/
	
	public Set<Proposition> getPropositions() {
		return propositions;
	}
	public void setPropositions(Set<Proposition> propositions) {
		this.propositions = propositions;
	}
	public Question getQuestion() {
		return question;
	}
	public void setQuestion(Question question) {
		this.question = question;
	}
	
	

	public Set<VoteCategorie> getVotesCategories() {
		return votesCategories;
	}

	public void setVoteCategorie(Set<VoteCategorie> votesCategories) {
		this.votesCategories = votesCategories;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
 
        if (o == null || getClass() != o.getClass())
            return false;
 
        Vote post = (Vote) o;
        return Objects.equals(id, post.id);
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
	
	

}

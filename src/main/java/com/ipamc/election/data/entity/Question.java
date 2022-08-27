package com.ipamc.election.data.entity;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
	private LocalDateTime dateVotes;
	
	
	public Question(String intitule, Boolean multiChoice, Boolean propositionRequired) {
		this.intitule = intitule;
		this.propositionRequired = propositionRequired;
		this.multiChoice = multiChoice;
		isActive = false;
	}
	
	public Question(String intitule, Set<Proposition> propositions, Set<Categorie> categories, Boolean multiChoice) {
		this.intitule = intitule;
		this.propositions = propositions;
		this.categories = categories;
		this.multiChoice = multiChoice;
		isActive = false;
	}
	
	public Question() {
		isActive = false;
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
	
	public Categorie getCategorieByLibelle(String libelle) {
		for(Categorie cat : categories) {
			if(cat.getLibelle().equals(libelle)) {
				return cat;
			}
		}
		return null;
	}
	
	public Boolean jureHasVoted(Jure jure) {
		for(Vote vote : votes) {
			if(vote.getJure().equals(jure))
				return true;
		}
		return false;
	}
	
	
	public void addVoteTime() {
		dateVotes = (OffsetDateTime.now( ZoneOffset.UTC )).toLocalDateTime() ;
	}
	
	public Boolean containsPropositions() {
		return propositions.size() > 0;
	}
	
	public Boolean containsNotes() {
		return getCategorieByLibelle("Note") != null;
	}

	public Map<Proposition, Integer> propositionsRanked(){
		Map<Proposition, Integer> unsortMap = new HashMap<Proposition, Integer>();
		for(Vote vote : votes) {
			if(vote.getPropositions().size()!=0) {
				for(Proposition proposition : vote.getPropositions()) {
					int currentValue = 0;
					if (unsortMap.containsKey(proposition)) {
						currentValue = unsortMap.get(proposition);
					}
					unsortMap.put(proposition, currentValue+1); 
				}
			}
		}
		LinkedHashMap<Proposition, Integer> sortMap = 
				unsortMap.entrySet()
				.stream()             
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.collect(Collectors.toMap(e -> e.getKey(), 
						e -> e.getValue(), 
						(e1, e2) -> null, // or throw an exception
						() -> new LinkedHashMap<Proposition, Integer>()));
		return sortMap;	
	}
	// Return sum of notes or -1 if there is no notes
	public int amountNotes() {
		int sumNote = 0;
		int cpt = 0;
		for(Vote vote : votes) {
			for(VoteCategorie vc : vote.getVotesCategories()) {
				if(vc.isNoteCategory()) {
					sumNote+=Integer.valueOf(vc.getReponse());
					cpt++;
					break;
				}
			}
		}
		// Means that there are no notes for any of thoses votes
		if(cpt == 0) {
			return -1;
		}else {
			return sumNote/cpt;
		}
	}
	
	public int getMaxValueNote() {
		int value = 0;
		if(votes.size() > 0) {
			for(Vote vote : votes) {
				for(Categorie cat : vote.getQuestion().getCategories()) {
					if(cat.isNoteCategory()) {
						return cat.getValeur();
					}
				}
				return value;
			}
		}
		return value;
	}
	
	public LocalDateTime getDateVotes() {
		return dateVotes;
	}

}

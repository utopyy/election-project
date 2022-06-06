package com.ipamc.election.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@Entity
@Table(name = "Votes_categories")
public class VoteCategorie {
	
	@EmbeddedId
	private VoteCategorieId id;

	@ManyToOne
	@MapsId("idVote")
	@JoinColumn(name = "id_vote")
	private Vote vote;
	
	@ManyToOne
	@MapsId("idCategorie")
	@JoinColumn(name = "id_categorie")
	private Categorie categorie;
	
	@Column(name = "reponse")
	private String reponse;
	
	public VoteCategorie() {}
	
	

	public VoteCategorie(Vote vote, Categorie categorie, String reponse) {
		this.id = new VoteCategorieId(vote.getId(), categorie.getId());
		this.vote = vote;
		this.categorie = categorie;
		this.reponse = reponse;
	}



	public Vote getVote() {
		return vote;
	}

	public void setVote(Vote vote) {
		this.vote = vote;
	}

	public Categorie getCategorie() {
		return categorie;
	}

	public void setCategorie(Categorie categorie) {
		this.categorie = categorie;
	}

	public String getReponse() {
		return reponse;
	}

	public void setReponse(String reponse) {
		this.reponse = reponse;
	}

	
	
}

package com.ipamc.election.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class VoteCategorieId implements Serializable {

	@Column(name = "id_vote")
	private Long idVote;
	
	@Column(name = "id_categorie")
	private Long idCategorie;
	
	public VoteCategorieId() {}

	public Long getIdVote() {
		return idVote;
	}
	
	

	public VoteCategorieId(Long idVote, Long idCategorie) {
		super();
		this.idVote = idVote;
		this.idCategorie = idCategorie;
	}

	public void setIdVote(Long idVote) {
		this.idVote = idVote;
	}

	public Long getIdCategorie() {
		return idCategorie;
	}

	public void setIdCategorie(Long idCategorie) {
		this.idCategorie = idCategorie;
	}
	
	
}

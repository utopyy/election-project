package com.ipamc.election.data.entity;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.sun.istack.NotNull;


@Entity
@Table(name="Resultats_jury")

public class ResultatsJury {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@OneToMany(mappedBy="resultats", fetch = FetchType.EAGER, cascade=CascadeType.ALL)
	private Set<Vote> votes = new HashSet<>();
	@NotNull
	private LocalDateTime date;
	private Boolean estVerrouille;
	
	public ResultatsJury() {
		date = (OffsetDateTime.now( ZoneOffset.UTC )).toLocalDateTime() ;
		estVerrouille = false;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<Vote> getVotes() {
		return votes;
	}

	public void setVotes(Set<Vote> votes) {
		this.votes = votes;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public Boolean getEstVerrouille() {
		return estVerrouille;
	}

	public void setEstVerrouille(Boolean estVerrouille) {
		this.estVerrouille = estVerrouille;
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
		ResultatsJury other = (ResultatsJury) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
}

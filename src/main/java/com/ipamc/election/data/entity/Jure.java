package com.ipamc.election.data.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="Jures")

public class Jure {

	@Embeddable
	public static class JureId implements Serializable {

		@Column(name = "id_session")
		protected Long idSession;

		@Column(name = "id_utilisateur")
		protected Long idUser;

		public JureId() {
			
		}

		public JureId(Long idSession, Long idUser) {
			this.idSession = idSession;
			this.idUser = idUser;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((idSession == null) ? 0 : idSession.hashCode());
			result = prime * result
					+ ((idUser == null) ? 0 : idUser.hashCode());
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

			JureId other = (JureId) obj;

			if (idSession == null) {
				if (other.idSession != null)
					return false;
			} else if (!idSession.equals(other.idSession))
				return false;

			if (idUser == null) {
				if (other.idUser != null)
					return false;
			} else if (!idUser.equals(other.idUser))
				return false;

			return true;
		}
	}

	@EmbeddedId
	private JureId id;
	
	@ManyToOne
	@JoinColumn(name = "id_utilisateur", insertable = false, updatable = false)
	private User user;

	@ManyToOne
	@JoinColumn(name = "id_session", insertable = false, updatable = false)
	private Session session;
	
	@OneToMany(mappedBy="jure", fetch = FetchType.EAGER, cascade=CascadeType.ALL)
	private Set<Vote> votes = new HashSet<>();

	@Column
	private Boolean hasJoined;	 
	
	@Column
	private Boolean archived;
	
	public Jure() {}
	
	public Jure(Session session, User user) {
		this.id = new JureId(session.getId(), user.getId());
		this.session = session;
		this.user = user;
		hasJoined = false;
		archived = false;
		session.getJures().add(this);
		user.getJures().add(this);
	}

	public JureId getId() {
		return id;
	}

	public void setId(JureId id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public Boolean getHasJoined() {
		return hasJoined;
	}

	public void setHasJoined(Boolean hasJoined) {
		this.hasJoined = hasJoined;
	}
	
	public Set<Vote> getVotes() {
		return votes;
	}



	public void setVotes(Set<Vote> votes) {
		this.votes = votes;
	}
	
	public void addVote(Vote vote) {
		vote.setJure(this);
		votes.add(vote);
	}
	
	public String getPseudo() {
		return user.getPseudo();
	}
	
	public Boolean hasVoted(Question question) {
		for(Vote vote : votes) {
			if(vote.getQuestion().equals(question)) {
				return true;
			}
		}
		return false;
	}
	//for active question
	public Boolean getHasVoted() {
		for(Vote vote : votes) {
			if(vote.getQuestion().getIsActive()) {
				return true;
			}
		}
		return false;
	}

	

	public Boolean getArchived() {
		return archived;
	}

	public void setArchived(Boolean archived) {
		this.archived = archived;
	}
	
	public void removeVote(Vote vote) {
		votes.remove(vote);
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
		Jure other = (Jure) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
}

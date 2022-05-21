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
import javax.persistence.Table;
import javax.validation.constraints.Email;

@Entity
@Table(name="Utilisateurs")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;	
    private String username;
    private String motDePasse;
    @Email
    private String email;
    private String pseudo;
    private Boolean estCertifie;
    @ManyToMany(fetch = FetchType.LAZY,
    	      cascade = CascadeType.MERGE)
    @JoinTable(	name = "Users_roles", 
			joinColumns = @JoinColumn(name = "idUtilisateur"), 
			inverseJoinColumns = @JoinColumn(name = "idRole"))
    private Set<Role> roles = new HashSet<>();
    
	public User() {		
	}
	
	public User(String username, String email, String password) {
		this.username = username;
		this.email = email;
		this.motDePasse = password;
	}
		
	public String getUsername() {
		return username;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMotDePasse() {
		return motDePasse;
	}

	public void setMotDePasse(String motDePasse) {
		this.motDePasse = motDePasse;
	}


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPseudo() {
		return pseudo;
	}

	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}

	public Boolean getEstCertifie() {
		return estCertifie;
	}

	public void setEstCertifie(Boolean estCertifie) {
		this.estCertifie = estCertifie;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((estCertifie == null) ? 0 : estCertifie.hashCode());
		result = prime * result + id;
		result = prime * result + ((motDePasse == null) ? 0 : motDePasse.hashCode());
		result = prime * result + ((pseudo == null) ? 0 : pseudo.hashCode());
		result = prime * result + ((roles == null) ? 0 : roles.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
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
		User other = (User) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (estCertifie == null) {
			if (other.estCertifie != null)
				return false;
		} else if (!estCertifie.equals(other.estCertifie))
			return false;
		if (id != other.id)
			return false;
		if (motDePasse == null) {
			if (other.motDePasse != null)
				return false;
		} else if (!motDePasse.equals(other.motDePasse))
			return false;
		if (pseudo == null) {
			if (other.pseudo != null)
				return false;
		} else if (!pseudo.equals(other.pseudo))
			return false;
		if (roles == null) {
			if (other.roles != null)
				return false;
		} else if (!roles.equals(other.roles))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
}

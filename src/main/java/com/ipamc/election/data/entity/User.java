package com.ipamc.election.data.entity;


import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
}

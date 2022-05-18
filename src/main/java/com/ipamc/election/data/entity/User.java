package com.ipamc.election.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ipamc.election.data.Role;

import java.util.Set;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;


public class User extends AbstractEntity {

    private String username;
    private String motDePasse;
    private Role role;
    private String email;
    private String pseudo;
    private Boolean estCertifie;

    //private String profilePictureUrl;


	public String getUsername() {
		return username;
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

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
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

	/**public String getProfilePictureUrl() {
		return profilePictureUrl;
	}

	public void setProfilePictureUrl(String profilePictureUrl) {
		this.profilePictureUrl = profilePictureUrl;
	}*/

    
}

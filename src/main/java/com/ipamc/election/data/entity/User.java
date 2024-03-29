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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang3.RandomStringUtils;
import com.ipamc.election.data.EnumRole;
import com.ipamc.election.validators.ValidEmail;
import com.sun.istack.NotNull;


@Entity
@Table(name="utilisateurs", schema ="dbo")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
    private String username;
	@NotNull
    private String password;
    @NotNull
    @ValidEmail
    private String email;
    private String pseudo;
    private Boolean certified;
    @ManyToMany(fetch = FetchType.EAGER,
    	      cascade = CascadeType.MERGE)
    @JoinTable(	name = "users_roles", 
			joinColumns = @JoinColumn(name = "idutilisateur"), 
			inverseJoinColumns = @JoinColumn(name = "idrole"))
    private Set<Role> roles = new HashSet<>();
    @OneToMany(mappedBy="user", fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<Jure> jures = new HashSet<>();
    
    
    private String activationCode;
    private String resetPasswordToken;
    private Boolean active;
    
	public User() {	
		this.pseudo = null;
		this.certified = false;
		this.active = false;
		this.activationCode = RandomStringUtils.randomAlphanumeric(32);
	}
	
	
	
	public String getResetPasswordToken() {
		return resetPasswordToken;
	}



	public void setResetPasswordToken(String resetPasswordToken) {
		this.resetPasswordToken = resetPasswordToken;
	}



	public User(String username, String email, String password) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.pseudo = null;
		this.certified = false;
		this.active = false;
		this.activationCode = RandomStringUtils.randomAlphanumeric(32);
	}
	
	
		
	public Boolean getCertified() {
		return certified;
	}

	public void setCertified(Boolean certified) {
		this.certified = certified;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
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

	public Boolean certified() {
		return certified;
	}
	
	


	public String getActivationCode() {
		return activationCode;
	}

	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
	}

	public Boolean isActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}


	public Boolean getActive() {
		return active;
	}
	

	public Set<Jure> getJures() {
		return jures;
	}



	public void setJures(Set<Jure> jures) {
		this.jures = jures;
	}

	public Boolean isSuperAdmin() {
		for(Role role : roles) {
			if(role.getName().equals(EnumRole.ROLE_SUPER_ADMIN)) {
				return true;
			}
		}
		return false;
	}
	
	public int getNbVotes() {
		int cpt = 0;
		for(Jure jure : jures) {
			cpt += jure.getVotes().size();
		}
		return cpt;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
	
	
	
	
	
}

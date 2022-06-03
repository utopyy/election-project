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

import com.sun.istack.NotNull;

@Entity
@Table(name="Sessions")
public class Session {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;	
	@NotNull
    private String name;
	@ManyToMany(fetch = FetchType.LAZY,
	  	      cascade = CascadeType.MERGE)
	    @JoinTable (name = "Jures",
	    		joinColumns = @JoinColumn(name = "id_session"),
	    		inverseJoinColumns = @JoinColumn(name = "id_utilisateur"))
	Set<User> users = new HashSet<>();
	private Boolean isActive;
	@OneToMany(mappedBy = "session", cascade=CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<Question> questions = new HashSet<Question>(); 
	
	
	public Session() {}
	
	public Session(String name) {
		this.name = name;
	}
	public Session(String name, Set<User> users) {
		super();
		this.name = name;
		this.users = users;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Set<User> getUsers() {
		return users;
	}
	public void setUsers(Set<User> users) {
		this.users = users;
	}
	
	public void addUser(User user) {
		users.add(user);
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	
	public Boolean userAllowed(User user) {
		return users.contains(user);
	}

	public Set<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(Set<Question> questions) {
		this.questions = questions;
	}
	
	
	
	

}

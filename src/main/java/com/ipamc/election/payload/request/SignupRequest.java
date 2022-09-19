package com.ipamc.election.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.ipamc.election.validators.PasswordMatches;
import com.ipamc.election.validators.ValidEmail;

@PasswordMatches
public class SignupRequest {
	  @NotBlank
	  @Size(min = 3, max = 20)
	  private String username;

	  @NotBlank
	  @Size(max = 50)
	  @ValidEmail
	  private String email;


	  @NotBlank
	  @Size(min = 6, max = 40)
	  private String password;
	  private String matchingPassword;

	  public String getMatchingPassword() {
		return matchingPassword;
	}

	public void setMatchingPassword(String matchingPassword) {
		this.matchingPassword = matchingPassword;
	}

	public String getUsername() {
	    return username;
	  }

	  public void setUsername(String username) {
	    this.username = username;
	  }

	  public String getEmail() {
	    return email;
	  }

	  public void setEmail(String email) {
	    this.email = email;
	  }

	  public String getPassword() {
	    return password;
	  }

	  public void setPassword(String password) {
	    this.password = password;
	  }
	}

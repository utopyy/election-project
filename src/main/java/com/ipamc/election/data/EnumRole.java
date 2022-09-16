package com.ipamc.election.data;

public enum EnumRole {

	ROLE_USER ("Membre"), 
    ROLE_ADMIN ("Administrateur"), 
    ROLE_SUPER_ADMIN ("Super administrateur");
	
	private String name;
	
	private EnumRole(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

}



package com.ipamc.election.data;

public enum BroadcastMessageType {
	CRUD_SESSION("CRUD_SESSION"),
	CRUD_QUESTION("CRUD_QUESTION"),
	SEND_VOTE("SEND_VOTE"),
	SHOW_RESULTS("SHOW_RESULTS"),
	ADMIN_EDIT_QUESTION("ADMIN_EDIT_QUESTION");
	
	public final String label;
	
	BroadcastMessageType(String label){
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
}

package com.ipamc.election.data.entity;

public class CategorieGridDetails {

	private String libelleCat;
	private Boolean isActive;
	private Boolean isRequired;
	private Integer value;
	private Boolean qcm;


	// Commentaire constructor
	public CategorieGridDetails(Boolean isActive, Boolean isRequired) {
		libelleCat = "Commentaire";
		this.isActive = isActive;
		this.isRequired = isRequired;
	}
	
	
	// Note constructor
	public CategorieGridDetails(Boolean isActive, Boolean isRequired, Integer value) {
		libelleCat = "Note";
		this.isActive = isActive;
		this.isRequired = isRequired;
		if(isActive != false)
			this.value = value;
	}
	
	// Proposition constructor
	public CategorieGridDetails(Boolean isActive, Boolean isRequired, Boolean qcm) {
		libelleCat = "Propositions";
		this.isActive = isActive;
		this.isRequired = isRequired;
		this.qcm = qcm;
	}

	public String getLibelleCat() {
		return libelleCat;
	}

	public void setLibelleCat(String libelleCat) {
		this.libelleCat = libelleCat;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Boolean getIsRequired() {
		return isRequired;
	}

	public void setIsRequired(Boolean isRequired) {
		this.isRequired = isRequired;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public Boolean getQcm() {
		return qcm;
	}

	public void setQcm(Boolean qcm) {
		this.qcm = qcm;
	}	
	
	
}

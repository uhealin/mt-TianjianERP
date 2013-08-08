package com.matech.audit.service.employment.model;

public class Skills {
	private String id;
	private String motherTongue;
	private String foreignLanguage;
	private String computerSkills;
	private String special;
	private String linkUserId;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMotherTongue() {
		return motherTongue;
	}
	public void setMotherTongue(String motherTongue) {
		this.motherTongue = motherTongue;
	}
	public String getForeignLanguage() {
		return foreignLanguage;
	}
	public void setForeignLanguage(String foreignLanguage) {
		this.foreignLanguage = foreignLanguage;
	}
	public String getComputerSkills() {
		return computerSkills;
	}
	public void setComputerSkills(String computerSkills) {
		this.computerSkills = computerSkills;
	}
	public String getSpecial() {
		return special;
	}
	public void setSpecial(String special) {
		this.special = special;
	}
	public String getLinkUserId() {
		return linkUserId;
	}
	public void setLinkUserId(String linkUserId) {
		this.linkUserId = linkUserId;
	}
}

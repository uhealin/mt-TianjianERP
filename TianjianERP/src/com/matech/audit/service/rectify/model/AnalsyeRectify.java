package com.matech.audit.service.rectify.model;

public class AnalsyeRectify {
	
	private String projectID = "" ;
	private String autoId = "" ;
	private String subjectID = "" ;
	private String assItemID = "" ;
	private String sFullName = "" ;
	private String tokenID = "" ;
	private String assTotalName = "" ;
	private String sid = "" ;
	private String isSubject = "" ;
	private String dataName = "" ;
	private String subYearMonth = "" ;
	private String subMonth = "" ;
	private String analsyeBalance = "" ;
	
	private String property = "" ;
	
	private int direction = 1;
	
	public int getDirection() { 
		return direction;
	}
	public void setDirection(int direction) {
		this.direction = direction;
	}
	public String getAnalsyeBalance() {
		return analsyeBalance;
	}
	public void setAnalsyeBalance(String analsyeBalance) {
		this.analsyeBalance = analsyeBalance;
	}
	public String getAssItemID() {
		return assItemID;
	}
	public void setAssItemID(String assItemID) {
		this.assItemID = assItemID;
	}
	public String getAssTotalName() {
		return assTotalName;
	}
	public void setAssTotalName(String assTotalName) {
		this.assTotalName = assTotalName;
	}
	public String getAutoId() {
		return autoId;
	}
	public void setAutoId(String autoId) {
		this.autoId = autoId;
	}
	public String getDataName() {
		return dataName;
	}
	public void setDataName(String dataName) {
		this.dataName = dataName;
	}
	public String getIsSubject() {
		return isSubject;
	}
	public void setIsSubject(String isSubject) {
		this.isSubject = isSubject;
	}
	public String getProjectID() {
		return projectID;
	}
	public void setProjectID(String projectID) {
		this.projectID = projectID;
	}
	public String getSFullName() {
		return sFullName;
	}
	public void setSFullName(String fullName) {
		sFullName = fullName;
	}
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public String getSubjectID() {
		return subjectID;
	}
	public void setSubjectID(String subjectID) {
		this.subjectID = subjectID;
	}
	public String getSubMonth() {
		return subMonth;
	}
	public void setSubMonth(String subMonth) {
		this.subMonth = subMonth;
	}
	public String getSubYearMonth() {
		return subYearMonth;
	}
	public void setSubYearMonth(String subYearMonth) {
		this.subYearMonth = subYearMonth;
	}
	public String getTokenID() {
		return tokenID;
	}
	public void setTokenID(String tokenID) {
		this.tokenID = tokenID;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}


}

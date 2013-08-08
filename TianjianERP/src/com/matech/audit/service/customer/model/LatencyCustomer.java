package com.matech.audit.service.customer.model;

public class LatencyCustomer {
	private String autoid;
	private String customerId;//客户编号
	private String projectId;//项目编号
	private String projectInformation;//项目信息
	private String planTime;//预计时间
	private String viable;//可行性评估
	private String recoder;//记录人
	private String recodeTime;//记录时间
	private String nextDenote;//后继跟踪指示
	private String nextPrincipal;//继跟踪责任人
	private String denotePerson;//指示人
	private String denoteTime;//指示时间
	
	public String getAutoid() {
		return autoid;
	}
	public void setAutoid(String autoid) {
		this.autoid = autoid;
	}	
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getProjectInformation() {
		return projectInformation;
	}
	public void setProjectInformation(String projectInformation) {
		this.projectInformation = projectInformation;
	}
	public String getPlanTime() {
		return planTime;
	}
	public void setPlanTime(String planTime) {
		this.planTime = planTime;
	}
	public String getViable() {
		return viable;
	}
	public void setViable(String viable) {
		this.viable = viable;
	}
	public String getRecoder() {
		return recoder;
	}
	public void setRecoder(String recoder) {
		this.recoder = recoder;
	}
	public String getRecodeTime() {
		return recodeTime;
	}
	public void setRecodeTime(String recodeTime) {
		this.recodeTime = recodeTime;
	}
	public String getNextDenote() {
		return nextDenote;
	}
	public void setNextDenote(String nextDenote) {
		this.nextDenote = nextDenote;
	}
	public String getNextPrincipal() {
		return nextPrincipal;
	}
	public void setNextPrincipal(String nextPrincipal) {
		this.nextPrincipal = nextPrincipal;
	}
	public String getDenotePerson() {
		return denotePerson;
	}
	public void setDenotePerson(String denotePerson) {
		this.denotePerson = denotePerson;
	}
	public String getDenoteTime() {
		return denoteTime;
	}
	public void setDenoteTime(String denoteTime) {
		this.denoteTime = denoteTime;
	}
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
}

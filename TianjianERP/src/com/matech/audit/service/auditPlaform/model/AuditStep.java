package com.matech.audit.service.auditPlaform.model;

public class AuditStep {
	 
	private String projectId ;
	private String curDealUser ;
	private String preDealUser ;
	private String submitAuditTime ;
	private String backAuditTime ;
	private String taskArriveTime ;
	private String curState ;
	private String preState ;
	private String advice ;
	private String property ;
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	public String getCurDealUser() {
		return curDealUser;
	}
	public void setCurDealUser(String curDealUser) {
		this.curDealUser = curDealUser;
	}
	public String getPreDealUser() {
		return preDealUser;
	}
	public void setPreDealUser(String preDealUser) {
		this.preDealUser = preDealUser;
	}
	public String getSubmitAuditTime() {
		return submitAuditTime;
	}
	public void setSubmitAuditTime(String submitAuditTime) {
		this.submitAuditTime = submitAuditTime;
	}
	public String getTaskArriveTime() {
		return taskArriveTime;
	}
	public void setTaskArriveTime(String taskArriveTime) {
		this.taskArriveTime = taskArriveTime;
	}
	public String getPreState() {
		return preState;
	}
	public void setPreState(String preState) {
		this.preState = preState;
	}
	public String getAdvice() {
		return advice;
	}
	public void setAdvice(String advice) {
		this.advice = advice;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public String getBackAuditTime() {
		return backAuditTime;
	}
	public void setBackAuditTime(String backAuditTime) {
		this.backAuditTime = backAuditTime;
	}
	public String getCurState() {
		return curState;
	}
	public void setCurState(String curState) {
		this.curState = curState;
	}
}

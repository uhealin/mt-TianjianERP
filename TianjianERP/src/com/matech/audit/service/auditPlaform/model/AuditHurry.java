package com.matech.audit.service.auditPlaform.model;

public class AuditHurry {
	 
	private String autoId ;
	private String projectId ;
	private String reserveTime ;
	private String reason ;
	private String auditTime ;
	private String updateDate ;
	private String userName ;
	private String property ;
	
	public String getAutoId() {
		return autoId;
	}
	public void setAutoId(String autoId) {
		this.autoId = autoId;
	}
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	public String getReserveTime() {
		return reserveTime;
	}
	public void setReserveTime(String reserveTime) {
		this.reserveTime = reserveTime;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public String getAuditTime() {
		return auditTime;
	}
	public void setAuditTime(String auditTime) {
		this.auditTime = auditTime;
	}
	
}

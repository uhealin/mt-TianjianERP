package com.matech.audit.service.leaveCompany.model;

public class LeaveCompanyFlow {
	  private String processInstanceId;
	  private String uuid;
	  private String applyuser ;
	  private String applyDate;
	  private String state;
	  private String ctype;
	  private String property;
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getApplyuser() {
		return applyuser;
	}
	public void setApplyuser(String applyuser) {
		this.applyuser = applyuser;
	}
	public String getApplyDate() {
		return applyDate;
	}
	public void setApplyDate(String applyDate) {
		this.applyDate = applyDate;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCtype() {
		return ctype;
	}
	public void setCtype(String ctype) {
		this.ctype = ctype;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	  
}

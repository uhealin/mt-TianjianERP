package com.matech.audit.service.customer.model;

public class ConsultTxt {
	private int autoId;//记录编号
	private String customerId;//客户编号
	private String recordId;//走访记录编号
	private String state;//问题状态
	private String recordContent;//记录内容
	private String department;//责任部门
	private String person;//责任人
	private String untillTime;//解决期限
	private String manager;//分工人
	
	public int getAutoId() {
		return autoId;
	}
	public void setAutoId(int autoId) {
		this.autoId = autoId;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getRecordId() {
		return recordId;
	}
	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}
	public String getRecordContent() {
		return recordContent;
	}
	public void setRecordContent(String recordContent) {
		this.recordContent = recordContent;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getPerson() {
		return person;
	}
	public void setPerson(String person) {
		this.person = person;
	}
	public String getUntillTime() {
		return untillTime;
	}
	public void setUntillTime(String untillTime) {
		this.untillTime = untillTime;
	}
	public String getManager() {
		return manager;
	}
	public void setManager(String manager) {
		this.manager = manager;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
}

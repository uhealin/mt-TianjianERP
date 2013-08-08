package com.matech.audit.service.auditPlaform.model;

public class AuditConfig {
	
	private int id  ;
	private String cname = "" ;
	private String stepname = "" ;
	private String readonly = "" ;
	private String property = "" ;
	private String checked = "" ;
	public String getChecked() {
		return checked;
	}
	public void setChecked(String checked) {
		this.checked = checked;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCname() {
		return cname;
	}
	public void setCname(String cname) {
		this.cname = cname;
	}
	public String getStepname() {
		return stepname;
	}
	public void setStepname(String stepname) {
		this.stepname = stepname;
	}
	public String getReadonly() {
		return readonly;
	}
	public void setReadonly(String readonly) {
		this.readonly = readonly;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}

}

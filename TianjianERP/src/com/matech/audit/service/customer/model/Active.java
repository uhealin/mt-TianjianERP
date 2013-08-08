package com.matech.audit.service.customer.model;

public class Active {
	private String autoId;
	private String activeName;
	private String activeType;
	private String startDate;
	private String endDate;
	private String address;
	private String partners;
	private String createUser;
	private String createDepartmentId;
	private String createTime;
	
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getCreateDepartmentId() {
		return createDepartmentId;
	}
	public void setCreateDepartmentId(String createDepartmentId) {
		this.createDepartmentId = createDepartmentId;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getAutoId() {
		return autoId;
	}
	public void setAutoId(String autoId) {
		this.autoId = autoId;
	}
	public String getActiveName() {
		return activeName;
	}
	public void setActiveName(String activeName) {
		this.activeName = activeName;
	}
	public String getActiveType() {
		return activeType;
	}
	public void setActiveType(String activeType) {
		this.activeType = activeType;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPartners() {
		return partners;
	}
	public void setPartners(String partners) {
		this.partners = partners;
	}
	

}
